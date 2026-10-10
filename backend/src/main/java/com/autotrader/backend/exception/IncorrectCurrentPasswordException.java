package com.autotrader.backend.exception;

// Thrown when a password change is attempted with a wrong current password.
// Deliberately NOT InvalidCredentialsException: that one maps to 401, and the
// frontend plans to treat any 401 as "session expired, log out".
public class IncorrectCurrentPasswordException extends RuntimeException {

    public IncorrectCurrentPasswordException(String message) {
        super(message);
    }
}