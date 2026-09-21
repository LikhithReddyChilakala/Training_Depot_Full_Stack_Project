package com.trainingdepot.gear.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.trainingdepot.gear.checkout.CheckoutResult;
import com.trainingdepot.gear.checkout.PaymentVerificationResult;
import com.trainingdepot.gear.exception.EmptyKitException;
import com.trainingdepot.gear.exception.InsufficientStockException;
import com.trainingdepot.gear.kit.KitLine;
import com.trainingdepot.gear.kit.KitView;
import com.trainingdepot.gear.model.EquipmentCategory;
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
import com.trainingdepot.gear.service.impl.OrderServiceImpl;
import com.trainingdepot.gear.service.impl.RazorpayGateway;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private RazorpayGateway razorpayGateway;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(orderRepository, paymentRepository, productRepository, razorpayGateway);
    }

    private User user(int id) {
        User u = new User();
        u.setId(id);
        u.setUsername("member" + id);
        u.setRole(Role.USER);
        return u;
    }

    private Product product(int id, int stock) {
        Product p = new Product();
        p.setId(id);
        p.setName("Kettlebell " + id);
        p.setPrice(new BigDecimal("1000.00"));
        p.setCategory(EquipmentCategory.KETTLEBELLS);
        p.setStockQuantity(stock);
        p.setActive(true);
        return p;
    }

    private KitView kitViewOf(Product product, int quantity) {
        return new KitView(List.of(new KitLine(product, quantity, quantity)), List.of());
    }

    // ---- startCheckout ----

    @Test
    void startCheckout_emptyKit_throwsAndTouchesNothing() throws Exception {
        KitView emptyKit = new KitView(List.of(), List.of());

        assertThatThrownBy(() -> orderService.startCheckout(user(1), emptyKit))
                .isInstanceOf(EmptyKitException.class);

        verify(orderRepository, never()).save(any());
        verify(razorpayGateway, never()).createOrder(any(), anyString());
    }

    @Test
    void startCheckout_requestedMoreThanInStock_throwsBeforeCreatingAnything() throws Exception {
        Product product = product(1, 2);
        KitView kit = kitViewOf(product, 5); // wants 5, only 2 on the shelf

        assertThatThrownBy(() -> orderService.startCheckout(user(1), kit))
                .isInstanceOf(InsufficientStockException.class);

        verify(orderRepository, never()).save(any());
        verify(razorpayGateway, never()).createOrder(any(), anyString());
    }

    @Test
    void startCheckout_success_createsPendingOrderAndCreatedPayment() throws Exception {
        Product product = product(1, 10);
        KitView kit = kitViewOf(product, 2); // 2 x 1000.00 = 2000.00
        User buyer = user(7);

        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {
            Order o = inv.getArgument(0);
            o.setId(42L);
            return o;
        });
        when(razorpayGateway.createOrder(any(BigDecimal.class), anyString()))
                .thenReturn(new RazorpayGateway.RazorpayOrderResult("order_abc123", 200000L));
        when(razorpayGateway.getKeyId()).thenReturn("rzp_test_key");
        when(paymentRepository.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));

        CheckoutResult result = orderService.startCheckout(buyer, kit);

        assertThat(result.getInternalOrderId()).isEqualTo(42L);
        assertThat(result.getRazorpayOrderId()).isEqualTo("order_abc123");
        assertThat(result.getAmountInPaise()).isEqualTo(200000L);
        assertThat(result.getTotalAmount()).isEqualByComparingTo("2000.00");

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue().getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(orderCaptor.getValue().getOrderItems()).hasSize(1);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        assertThat(paymentCaptor.getValue().getStatus()).isEqualTo(PaymentStatus.CREATED);
        assertThat(paymentCaptor.getValue().getRazorpayOrderId()).isEqualTo("order_abc123");

        // Stock is never touched at checkout time - only verifyAndFinalize moves it.
        verify(productRepository, never()).findByIdForUpdate(any());
        verify(productRepository, never()).save(any());
    }

    // ---- verifyAndFinalize ----

    private Order pendingOrderWithItem(long orderId, Product product, int quantity) {
        User buyer = user(9);
        Order order = new Order();
        order.setId(orderId);
        order.setUser(buyer);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(product.getFinalPrice().multiply(BigDecimal.valueOf(quantity)));
        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setProductName(product.getName());
        item.setQuantity(quantity);
        item.setUnitPrice(product.getFinalPrice());
        order.addItem(item);
        return order;
    }

    private Payment createdPayment(Order order, String razorpayOrderId) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setRazorpayOrderId(razorpayOrderId);
        payment.setAmount(order.getTotalAmount());
        payment.setStatus(PaymentStatus.CREATED);
        return payment;
    }

    @Test
    void verifyAndFinalize_invalidSignature_cancelsWithoutTouchingStock() {
        Product product = product(1, 10);
        Order order = pendingOrderWithItem(42L, product, 2);
        Payment payment = createdPayment(order, "order_abc123");

        when(orderRepository.findById(42L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrder(order)).thenReturn(Optional.of(payment));
        when(razorpayGateway.verifySignature("order_abc123", "pay_1", "bad-signature")).thenReturn(false);

        PaymentVerificationResult result = orderService.verifyAndFinalize(42L, "pay_1", "order_abc123", "bad-signature");

        assertThat(result.isSuccess()).isFalse();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        verify(productRepository, never()).findByIdForUpdate(any());
        verify(productRepository, never()).save(any());
    }

    @Test
    void verifyAndFinalize_validSignatureAndStockAvailable_confirmsAndReducesStock() {
        Product product = product(1, 10);
        Order order = pendingOrderWithItem(42L, product, 2);
        Payment payment = createdPayment(order, "order_abc123");

        when(orderRepository.findById(42L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrder(order)).thenReturn(Optional.of(payment));
        when(razorpayGateway.verifySignature("order_abc123", "pay_1", "good-signature")).thenReturn(true);
        when(productRepository.findByIdForUpdate(1)).thenReturn(Optional.of(product));

        PaymentVerificationResult result = orderService.verifyAndFinalize(42L, "pay_1", "order_abc123", "good-signature");

        assertThat(result.isSuccess()).isTrue();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(payment.getRazorpayPaymentId()).isEqualTo("pay_1");
        assertThat(product.getStockQuantity()).isEqualTo(8); // 10 - 2
        verify(productRepository).save(product);
    }

    @Test
    void verifyAndFinalize_validSignatureButSoldOutSinceCheckout_cancelsAndNeverReducesStock() {
        Product product = product(1, 10);
        Order order = pendingOrderWithItem(42L, product, 2);
        Payment payment = createdPayment(order, "order_abc123");

        when(orderRepository.findById(42L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrder(order)).thenReturn(Optional.of(payment));
        when(razorpayGateway.verifySignature("order_abc123", "pay_1", "good-signature")).thenReturn(true);

        // Someone else bought the last units between checkout and this verification.
        Product nowOutOfStock = product(1, 1);
        when(productRepository.findByIdForUpdate(1)).thenReturn(Optional.of(nowOutOfStock));

        PaymentVerificationResult result = orderService.verifyAndFinalize(42L, "pay_1", "order_abc123", "good-signature");

        assertThat(result.isSuccess()).isFalse();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.FAILED);
        verify(productRepository, never()).save(any());
    }

    @Test
    void verifyAndFinalize_alreadyConfirmed_isIdempotentAndSkipsSignatureCheck() {
        Product product = product(1, 10);
        Order order = pendingOrderWithItem(42L, product, 2);
        order.setStatus(OrderStatus.CONFIRMED);

        when(orderRepository.findById(42L)).thenReturn(Optional.of(order));

        PaymentVerificationResult result = orderService.verifyAndFinalize(42L, "pay_1", "order_abc123", "sig");

        assertThat(result.isSuccess()).isTrue();
        verify(razorpayGateway, never()).verifySignature(any(), any(), any());
        verify(productRepository, never()).findByIdForUpdate(any());
    }

    @Test
    void verifyAndFinalize_razorpayOrderIdMismatch_cancelsWithoutVerifyingSignature() {
        Product product = product(1, 10);
        Order order = pendingOrderWithItem(42L, product, 2);
        Payment payment = createdPayment(order, "order_the_real_one");

        when(orderRepository.findById(42L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrder(order)).thenReturn(Optional.of(payment));

        PaymentVerificationResult result = orderService.verifyAndFinalize(42L, "pay_1", "order_a_different_one", "sig");

        assertThat(result.isSuccess()).isFalse();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(razorpayGateway, never()).verifySignature(any(), any(), any());
        verify(productRepository, never()).findByIdForUpdate(any());
    }

    @Test
    void getOrderForUser_rejectsAccessToAnotherMembersOrder() {
        Product product = product(1, 10);
        Order order = pendingOrderWithItem(42L, product, 2); // owned by user(9)
        order.setStatus(OrderStatus.CONFIRMED);
        when(orderRepository.findById(42L)).thenReturn(Optional.of(order));

        User someoneElse = user(123);

        assertThatThrownBy(() -> orderService.getOrderForUser(42L, someoneElse))
                .isInstanceOf(com.trainingdepot.gear.exception.OrderNotFoundException.class);
    }
}
