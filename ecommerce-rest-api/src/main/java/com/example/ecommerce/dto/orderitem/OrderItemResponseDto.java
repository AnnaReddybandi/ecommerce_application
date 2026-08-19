package com.example.ecommerce.dto.orderitem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderItemResponseDto(

        Long id,
        Long orderId,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal price,
        BigDecimal subtotal,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}