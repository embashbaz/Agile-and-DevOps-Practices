package com.example.backend.exceptions;


// Triggered when logging in with an incorrect password
public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String message) {
        super(message);
    }
}
