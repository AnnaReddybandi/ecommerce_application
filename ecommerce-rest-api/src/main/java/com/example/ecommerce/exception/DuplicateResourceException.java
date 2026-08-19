package com.example.ecommerce.exception;

/**
 * Thrown when a duplicate resource is created.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}