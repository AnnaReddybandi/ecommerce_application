package com.example.ecommerce.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CustomerRequestDto(

        @NotBlank(message = "Customer name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,

        @NotBlank(message = "Phone is required")
        @Pattern(
                regexp = "^[0-9]{10}$",
                message = "Phone should be 10 digits"
        )
        String phone,

        @NotBlank(message = "Address is required")
        String address
) {
}