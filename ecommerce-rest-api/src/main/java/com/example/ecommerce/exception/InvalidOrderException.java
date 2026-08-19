package com.example.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * InvalidOrderException
 * Thrown when an invalid order operation is performed.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidOrderException extends RuntimeException {

    public InvalidOrderException(String message) {
        super(message);
    }
}
