package com.example.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * InsufficientStockException
 * Thrown when requested product stock is insufficient.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String message) {
        super(message);
    }
}
