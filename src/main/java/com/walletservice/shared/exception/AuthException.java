package com.walletservice.shared.exception;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}