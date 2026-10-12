package com.example.backend.exceptions;

// Triggered when registering an existing email
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
