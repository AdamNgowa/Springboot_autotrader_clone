package com.autotrader.backend.exception;

public class UnauthorizedConversationAccessException
        extends RuntimeException {

    public UnauthorizedConversationAccessException(String message) {
        super(message);
    }
}