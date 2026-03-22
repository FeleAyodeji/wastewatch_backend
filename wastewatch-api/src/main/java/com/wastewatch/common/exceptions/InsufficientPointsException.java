package com.wastewatch.common.exceptions;

public class InsufficientPointsException extends RuntimeException {
    public InsufficientPointsException(int required, int actual) {
        super("You need " + required + " points. You have " + actual + ".");
    }
}