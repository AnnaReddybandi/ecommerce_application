package com.example.ecommerce.dto.cartitem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CartItemResponseDto(

        Long id,

        Long cartId,

        Long productId,

        String productName,

        Integer quantity,

        BigDecimal price,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}