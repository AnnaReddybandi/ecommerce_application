package com.example.ecommerce.dto.order;

import com.example.ecommerce.entity.enums.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CheckoutRequestDto(

        @NotNull(message = "Customer ID is required")
        Long customerId,

        @NotBlank(message = "Shipping address is required")
        String shippingAddress,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        String notes

) {
    public CheckoutRequestDto(Long customerId, String shippingAddress, PaymentMethod paymentMethod) {
        this(customerId, shippingAddress, paymentMethod, null);
    }
}