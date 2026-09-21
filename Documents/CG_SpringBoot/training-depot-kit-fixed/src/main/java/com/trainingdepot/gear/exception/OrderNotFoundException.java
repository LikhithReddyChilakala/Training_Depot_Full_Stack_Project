package com.trainingdepot.gear.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(long id) {
        super("No order with id " + id);
    }
}
