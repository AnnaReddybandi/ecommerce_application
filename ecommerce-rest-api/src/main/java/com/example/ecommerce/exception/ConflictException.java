package com.example.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a request conflicts with existing data.
 *
 * Example:
 * - Duplicate customer email
 * - Duplicate product in cart
 * - Duplicate payment for an order
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}