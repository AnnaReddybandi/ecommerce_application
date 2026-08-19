package com.example.ecommerce.service;

import com.example.ecommerce.dto.product.ProductRequestDto;
import com.example.ecommerce.dto.product.ProductResponseDto;
import com.example.ecommerce.entity.enums.ProductCategory;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    ProductResponseDto create(ProductRequestDto request);

    ProductResponseDto getById(Long id);

    List<ProductResponseDto> getAll();

    ProductResponseDto update(Long id, ProductRequestDto request);

    void delete(Long id);

    ProductResponseDto reduceStock(Long id, Integer quantity);

    ProductResponseDto increaseStock(Long id, Integer quantity);

    ProductResponseDto uploadImage(Long id, MultipartFile file);

    List<ProductResponseDto> queryByMinimumStock(Integer minimumStock);

    List<ProductResponseDto> queryByPriceRange(
            BigDecimal min,
            BigDecimal max
    );

    List<ProductResponseDto> queryByCategory(
            ProductCategory category
    );

    List<ProductResponseDto> getByPriceRange(
            BigDecimal min,
            BigDecimal max
    );

    List<ProductResponseDto> getLowStockProducts(
            Integer threshold
    );

    List<ProductResponseDto> getByCategory(
            ProductCategory category
    );
}