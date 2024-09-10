package com.example.bankdemo.exception;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
