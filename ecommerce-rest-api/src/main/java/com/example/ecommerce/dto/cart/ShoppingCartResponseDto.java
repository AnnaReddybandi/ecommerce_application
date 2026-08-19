package com.example.ecommerce.dto.cart;

import com.example.ecommerce.dto.cartitem.CartItemResponseDto;

import java.time.LocalDateTime;
import java.util.List;

public record ShoppingCartResponseDto(

        Long id,

        Long customerId,

        String customerName,

        List<CartItemResponseDto> cartItems,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}