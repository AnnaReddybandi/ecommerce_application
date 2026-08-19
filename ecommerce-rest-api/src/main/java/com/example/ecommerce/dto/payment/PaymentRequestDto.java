package com.example.ecommerce.dto.payment;

import com.example.ecommerce.entity.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequestDto(

        @NotNull(message = "Order ID is required")
        Long orderId,

        @NotNull(message = "Amount is required")
        @DecimalMin(
                value = "0.01",
                message = "Amount must be greater than zero"
        )
        BigDecimal amount,

        @NotNull(message = "Payment method is required")
        PaymentMethod method,

        String transactionId,

        String notes
) {
}