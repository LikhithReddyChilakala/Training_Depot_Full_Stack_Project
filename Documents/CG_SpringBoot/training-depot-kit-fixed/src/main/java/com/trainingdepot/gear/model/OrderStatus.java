package com.trainingdepot.gear.model;

/**
 * Lifecycle of a Depot order.
 * PENDING   - created the moment checkout starts a Razorpay order; stock is
 *             untouched at this point.
 * CONFIRMED - payment signature verified and stock was successfully reduced.
 * CANCELLED - payment failed/was invalid, or stock ran out before the
 *             verified payment could be fulfilled. Never implies stock was
 *             touched.
 */
public enum OrderStatus {
    PENDING,
    CONFIRMED,
    CANCELLED
}
