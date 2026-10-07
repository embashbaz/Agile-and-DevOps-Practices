package com.example.backend.exceptions;

// Triggered when logging in with an email that doesn't exist
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
