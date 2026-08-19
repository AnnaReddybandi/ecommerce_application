package com.example.ecommerce.dto.product;

import com.example.ecommerce.entity.enums.ProductCategory;
import com.example.ecommerce.entity.enums.ProductStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequestDto(

        @NotBlank(message = "Product name is required")
        String name,

        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(
                value = "0.01",
                message = "Price must be greater than zero"
        )
        BigDecimal price,

        @NotNull(message = "Stock is required")
        @Min(
                value = 0,
                message = "Stock cannot be negative"
        )
        Integer stock,

        @NotNull(message = "Category is required")
        ProductCategory category,

        ProductStatus status,

        String imageUrl
) {
    public ProductRequestDto(String name, String description, BigDecimal price, Integer stock, ProductCategory category) {
        this(name, description, price, stock, category, ProductStatus.ACTIVE, null);
    }
}