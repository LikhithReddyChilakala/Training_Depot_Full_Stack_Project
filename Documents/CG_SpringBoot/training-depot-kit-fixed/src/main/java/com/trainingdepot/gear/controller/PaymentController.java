package com.trainingdepot.gear.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.trainingdepot.gear.checkout.PaymentVerificationResult;
import com.trainingdepot.gear.model.Order;
import com.trainingdepot.gear.model.OrderStatus;
import com.trainingdepot.gear.service.EmailService;
import com.trainingdepot.gear.service.KitService;
import com.trainingdepot.gear.service.OrderService;

@Controller
public class PaymentController {

    private final OrderService orderService;
    private final KitService kitService;
    private final EmailService emailService;

    public PaymentController(
            OrderService orderService,
            KitService kitService,
            EmailService emailService) {

        this.orderService = orderService;
        this.kitService = kitService;
        this.emailService = emailService;
    }

    @PostMapping("/payment/verify")
    public String verify(
            @RequestParam long internalOrderId,
            @RequestParam("razorpay_payment_id") String razorpayPaymentId,
            @RequestParam("razorpay_order_id") String razorpayOrderId,
            @RequestParam("razorpay_signature") String razorpaySignature,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        PaymentVerificationResult result =
                orderService.verifyAndFinalize(
                        internalOrderId,
                        razorpayPaymentId,
                        razorpayOrderId,
                        razorpaySignature);

        Order order = result.getOrder();

        if (result.isSuccess()
                && order.getStatus() == OrderStatus.CONFIRMED) {

            // Payment succeeded and the order is confirmed.
            // Clear the user's kit.
            kitService.clear(session);

            /*
             * Email is secondary to the payment/order transaction.
             * If email fails, the successful order must NOT become
             * a 500 error for the customer.
             */
            try {
                emailService.sendOrderConfirmation(
                        order.getUser(),
                        order);
            } catch (Exception e) {
                // Log the problem but do not interrupt the successful flow.
                System.err.println(
                        "Order confirmation email could not be sent: "
                        + e.getMessage());
            }

            // Successful payment → HOME PAGE
            return "redirect:/";
        }

        // Payment verification failed.
        redirectAttributes.addFlashAttribute(
                "error",
                result.getFailureReason());

        return "redirect:/checkout";
    }
}