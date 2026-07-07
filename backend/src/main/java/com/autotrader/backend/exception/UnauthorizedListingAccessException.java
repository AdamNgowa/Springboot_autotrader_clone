package com.autotrader.backend.exception;

public class UnauthorizedListingAccessException extends RuntimeException {
    public UnauthorizedListingAccessException(String message) {
        super(message);
    }
}
