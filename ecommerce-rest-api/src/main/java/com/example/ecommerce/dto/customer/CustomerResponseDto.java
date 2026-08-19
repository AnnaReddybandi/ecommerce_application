package com.example.ecommerce.dto.customer;

import java.time.LocalDateTime;

public record CustomerResponseDto(

        Long id,

        String name,

        String email,

        String phone,

        String address,

        LocalDateTime createdAt,

        LocalDateTime updatedAt
) {
}