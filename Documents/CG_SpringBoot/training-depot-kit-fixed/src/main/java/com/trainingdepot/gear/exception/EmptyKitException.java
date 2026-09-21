package com.trainingdepot.gear.exception;

public class EmptyKitException extends RuntimeException {
    public EmptyKitException() {
        super("The kit is empty");
    }
}
