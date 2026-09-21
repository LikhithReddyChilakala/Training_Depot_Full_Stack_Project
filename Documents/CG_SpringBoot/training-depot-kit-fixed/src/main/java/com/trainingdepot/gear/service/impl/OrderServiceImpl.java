package com.trainingdepot.gear.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.razorpay.RazorpayException;
import com.trainingdepot.gear.checkout.CheckoutResult;
import com.trainingdepot.gear.checkout.PaymentVerificationResult;
import com.trainingdepot.gear.exception.EmptyKitException;
import com.trainingdepot.gear.exception.InsufficientStockException;
import com.trainingdepot.gear.exception.InvalidPaymentException;
import com.trainingdepot.gear.exception.OrderNotFoundException;
import com.trainingdepot.gear.kit.KitLine;
import com.trainingdepot.gear.kit.KitView;
import com.trainingdepot.gear.model.Order;
import com.trainingdepot.gear.model.OrderItem;
import com.trainingdepot.gear.model.OrderStatus;
import com.trainingdepot.gear.model.Payment;
import com.trainingdepot.gear.model.PaymentStatus;
import com.trainingdepot.gear.model.Product;
import com.trainingdepot.gear.model.Role;
import com.trainingdepot.gear.model.User;
import com.trainingdepot.gear.repository.OrderRepository;
import com.trainingdepot.gear.repository.PaymentRepository;
import com.trainingdepot.gear.repository.ProductRepository;
import com.trainingdepot.gear.service.OrderService;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final RazorpayGateway razorpayGateway;

    public OrderServiceImpl(OrderRepository orderRepository, PaymentRepository paymentRepository,
            ProductRepository productRepository, RazorpayGateway razorpayGateway) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.productRepository = productRepository;
        this.razorpayGateway = razorpayGateway;
    }

    @Override
    @Transactional
    public CheckoutResult startCheckout(User user, KitView kitView) {
        if (kitView.isEmpty()) {
            throw new EmptyKitException();
        }
        // Friendly pre-check. The authoritative check happens again, under a
        // row lock, inside verifyAndFinalize - this one just avoids sending
        // someone to Razorpay for an order that's already doomed.
        for (KitLine line : kitView.getLines()) {
            if (line.getRequestedQuantity() > line.getProduct().getStockQuantity()) {
                throw new InsufficientStockException(line.getProduct().getName(),
                        line.getProduct().getStockQuantity(), line.getRequestedQuantity());
            }
        }

        BigDecimal total = kitView.getSubtotal();

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(total);
        for (KitLine line : kitView.getLines()) {
            OrderItem item = new OrderItem();
            item.setProduct(line.getProduct());
            item.setProductName(line.getProduct().getName());
            item.setQuantity(line.getQuantity());
            item.setUnitPrice(line.getUnitPrice());
            order.addItem(item);
        }
        Order savedOrder = orderRepository.save(order);

        RazorpayGateway.RazorpayOrderResult rpOrder;
        try {
            rpOrder = razorpayGateway.createOrder(total, "depot-order-" + savedOrder.getId());
        } catch (RazorpayException e) {
            // Transaction rolls back - no dangling PENDING order is left behind.
            throw new RuntimeException("Could not start the Razorpay order: " + e.getMessage(), e);
        }

        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setRazorpayOrderId(rpOrder.getRazorpayOrderId());
        payment.setAmount(total);
        payment.setStatus(PaymentStatus.CREATED);
        paymentRepository.save(payment);

        return new CheckoutResult(savedOrder.getId(), rpOrder.getRazorpayOrderId(), razorpayGateway.getKeyId(),
                rpOrder.getAmountInPaise(), total);
    }

    @Override
    @Transactional
    public PaymentVerificationResult verifyAndFinalize(long internalOrderId, String razorpayPaymentId,
            String razorpayOrderId, String razorpaySignature) {

        Order order = orderRepository.findById(internalOrderId)
                .orElseThrow(() -> new OrderNotFoundException(internalOrderId));

        // Idempotent: a page refresh or a duplicated callback should not redo
        // stock reduction or send a second confirmation.
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            return PaymentVerificationResult.success(order);
        }
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return PaymentVerificationResult.failure(order, "This order was already cancelled.");
        }

        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() -> new InvalidPaymentException("Order " + order.getId() + " has no payment record"));

        if (!payment.getRazorpayOrderId().equals(razorpayOrderId)) {
            return cancel(order, payment, "The payment reference did not match this order.");
        }

        boolean signatureValid = razorpayGateway.verifySignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);
        if (!signatureValid) {
            return cancel(order, payment, "Payment verification failed.");
        }

        // Re-check stock under a row lock - this is the only place stock ever
        // moves. Lock every line first (so we hold every lock we'll need
        // before reducing anything), then only reduce once all are confirmed
        // sufficient.
        Map<OrderItem, Product> lockedByItem = new LinkedHashMap<>();
        for (OrderItem item : order.getOrderItems()) {
            Product locked = productRepository.findByIdForUpdate(item.getProduct().getId()).orElse(null);
            if (locked == null || locked.getStockQuantity() < item.getQuantity()) {
                return cancel(order, payment,
                        "Payment was received, but \"" + item.getProductName()
                                + "\" sold out just before confirmation. Our team will reach out about a refund.");
            }
            lockedByItem.put(item, locked);
        }
        for (Map.Entry<OrderItem, Product> entry : lockedByItem.entrySet()) {
            Product locked = entry.getValue();
            locked.setStockQuantity(locked.getStockQuantity() - entry.getKey().getQuantity());
            productRepository.save(locked);
        }

        payment.setRazorpayPaymentId(razorpayPaymentId);
        payment.setRazorpaySignature(razorpaySignature);
        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setVerifiedAt(LocalDateTime.now());
        payment.setMethod(razorpayGateway.fetchPaymentMethod(razorpayPaymentId));
        paymentRepository.save(payment);

        order.setStatus(OrderStatus.CONFIRMED);
        order.setConfirmedAt(LocalDateTime.now());
        orderRepository.save(order);

        return PaymentVerificationResult.success(order);
    }

    private PaymentVerificationResult cancel(Order order, Payment payment, String reason) {
        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
        return PaymentVerificationResult.failure(order, reason);
    }

    @Override
    public List<Order> getOrdersForUser(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .filter(o -> o.getStatus() != OrderStatus.PENDING)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderForUser(long orderId, User requestingUser) {

        Order order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        boolean owns = order.getUser().getId().equals(requestingUser.getId());
        boolean isAdmin = requestingUser.getRole() == Role.ADMIN;

        if (!owns && !isAdmin) {
            throw new OrderNotFoundException(orderId);
        }

        return order;
    }

    @Override
    public List<Order> getAllOrdersForAdmin() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }
}
