package com.example.ecommerce.dto.cart;


import jakarta.validation.constraints.NotNull;

public record ShoppingCartRequestDto(

        @NotNull(message = "Customer ID is required")
        Long customerId

) {
}