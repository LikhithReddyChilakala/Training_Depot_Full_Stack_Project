package com.trainingdepot.gear.service;

import java.util.List;

import com.trainingdepot.gear.checkout.CheckoutResult;
import com.trainingdepot.gear.checkout.PaymentVerificationResult;
import com.trainingdepot.gear.kit.KitView;
import com.trainingdepot.gear.model.Order;
import com.trainingdepot.gear.model.User;

public interface OrderService {

    /**
     * Validates the already-resolved kit view against live stock, computes
     * the authoritative total, opens a Razorpay order, and persists a
     * PENDING Order + CREATED Payment. Stock is not touched here.
     */
    CheckoutResult startCheckout(User user, KitView kitView);

    /**
     * Verifies the Razorpay signature and, only if valid and stock still
     * suffices, reduces stock and confirms the order in one transaction.
     * Never reduces stock on a failed/invalid attempt.
     */
    PaymentVerificationResult verifyAndFinalize(long internalOrderId, String razorpayPaymentId,
            String razorpayOrderId, String razorpaySignature);

    List<Order> getOrdersForUser(User user);

    /** Throws OrderNotFoundException if missing, or if it belongs to a different non-admin user. */
    Order getOrderForUser(long orderId, User requestingUser);

    List<Order> getAllOrdersForAdmin();
}
