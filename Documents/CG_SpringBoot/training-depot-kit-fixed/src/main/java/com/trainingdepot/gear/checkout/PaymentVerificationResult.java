package com.trainingdepot.gear.checkout;

import com.trainingdepot.gear.model.Order;

/** Outcome of verifying a Razorpay callback and (if valid) finalizing the order. */
public class PaymentVerificationResult {

    private final boolean success;
    private final Order order;
    private final String failureReason;

    private PaymentVerificationResult(boolean success, Order order, String failureReason) {
        this.success = success;
        this.order = order;
        this.failureReason = failureReason;
    }

    public static PaymentVerificationResult success(Order order) {
        return new PaymentVerificationResult(true, order, null);
    }

    public static PaymentVerificationResult failure(Order order, String reason) {
        return new PaymentVerificationResult(false, order, reason);
    }

    public boolean isSuccess() {
        return success;
    }

    public Order getOrder() {
        return order;
    }

    public String getFailureReason() {
        return failureReason;
    }
}
