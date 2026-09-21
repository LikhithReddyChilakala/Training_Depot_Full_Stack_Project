package com.trainingdepot.gear.model;

/**
 * Kept separate from OrderStatus on purpose (see spec: payment state and
 * order state are two different questions - "was the money captured?" vs
 * "was the order fulfilled?").
 */
public enum PaymentStatus {
    CREATED,
    SUCCESS,
    FAILED
}
