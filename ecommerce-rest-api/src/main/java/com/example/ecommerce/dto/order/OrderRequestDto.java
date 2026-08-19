package com.example.ecommerce.dto.order;

import com.example.ecommerce.dto.orderitem.OrderItemRequestDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderRequestDto(

        @NotNull(message = "Customer ID is required")
        Long customerId,

        @NotBlank(message = "Shipping address is required")
        String shippingAddress,

        @NotEmpty(message = "Order must contain at least one item")
        @Valid
        List<OrderItemRequestDto> items

) {
}