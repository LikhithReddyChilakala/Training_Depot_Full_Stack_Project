package com.trainingdepot.gear.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(int id) {
        super("No equipment record with id " + id);
    }
}
