package com.example.ecommerce.service;

import com.example.ecommerce.dto.payment.PaymentRequestDto;
import com.example.ecommerce.dto.payment.PaymentResponseDto;
import com.example.ecommerce.entity.enums.PaymentStatus;

import java.util.List;

/**
 * PaymentService
 *
 * Service layer for payment operations.
 */
public interface PaymentService {

    /**
     * Create payment record.
     */
    PaymentResponseDto create(
            PaymentRequestDto request
    );

    /**
     * Get payment by ID.
     */
    PaymentResponseDto getById(Long id);

    /**
     * Get all payments.
     */
    List<PaymentResponseDto> getAll();

    /**
     * Update payment.
     */
    PaymentResponseDto update(
            Long id,
            PaymentRequestDto request
    );

    /**
     * Delete payment.
     */
    void delete(Long id);

    /**
     * Process payment.
     */
    PaymentResponseDto processPayment(Long id);

    /**
     * Get payments by status.
     */
    List<PaymentResponseDto> getByStatus(
            PaymentStatus status
    );

    /**
     * Get payment by order ID.
     */
    PaymentResponseDto getByOrderId(Long orderId);
}