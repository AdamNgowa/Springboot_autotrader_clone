package com.autotrader.backend.exception;

public class AuthenticatedUserNotFoundException extends RuntimeException {

    public AuthenticatedUserNotFoundException(String message) {
        super(message);
    }
}