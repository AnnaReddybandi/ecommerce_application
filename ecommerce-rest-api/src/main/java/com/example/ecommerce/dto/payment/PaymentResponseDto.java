package com.example.ecommerce.dto.payment;

import com.example.ecommerce.entity.enums.PaymentMethod;
import com.example.ecommerce.entity.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponseDto(

        Long id,

        Long orderId,

        LocalDateTime paymentDate,

        BigDecimal amount,

        PaymentMethod method,

        PaymentStatus status,

        String transactionId,

        String notes,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}