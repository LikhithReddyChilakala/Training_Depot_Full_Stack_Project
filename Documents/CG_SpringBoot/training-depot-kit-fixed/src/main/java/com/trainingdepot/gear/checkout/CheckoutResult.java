package com.trainingdepot.gear.checkout;

import java.math.BigDecimal;

/** Everything checkout.jsp needs to render the Razorpay Checkout.js widget. */
public class CheckoutResult {

    private final long internalOrderId;
    private final String razorpayOrderId;
    private final String razorpayKeyId;
    private final long amountInPaise;
    private final BigDecimal totalAmount;

    public CheckoutResult(long internalOrderId, String razorpayOrderId, String razorpayKeyId,
            long amountInPaise, BigDecimal totalAmount) {
        this.internalOrderId = internalOrderId;
        this.razorpayOrderId = razorpayOrderId;
        this.razorpayKeyId = razorpayKeyId;
        this.amountInPaise = amountInPaise;
        this.totalAmount = totalAmount;
    }

    public long getInternalOrderId() {
        return internalOrderId;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public String getRazorpayKeyId() {
        return razorpayKeyId;
    }

    public long getAmountInPaise() {
        return amountInPaise;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
