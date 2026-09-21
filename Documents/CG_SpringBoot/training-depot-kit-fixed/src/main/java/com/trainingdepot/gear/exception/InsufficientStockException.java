package com.trainingdepot.gear.exception;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(String productName, int available, int requested) {
        super(String.format("Only %d unit(s) of \"%s\" left, but %d requested", available, productName, requested));
    }
}
