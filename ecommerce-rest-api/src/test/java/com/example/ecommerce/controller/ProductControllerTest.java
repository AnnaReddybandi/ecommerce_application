package com.example.ecommerce.controller;

import com.example.ecommerce.controller.v1.ProductController;
import com.example.ecommerce.dto.product.ProductRequestDto;
import com.example.ecommerce.dto.product.ProductResponseDto;
import com.example.ecommerce.entity.enums.ProductCategory;
import com.example.ecommerce.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @Test
    @DisplayName("POST /api/v1/products - Create product success")
    void createProduct_Success() throws Exception {
        ProductRequestDto request = new ProductRequestDto(
                "Gaming Laptop", "High performance", new BigDecimal("1499.99"), 15,
                ProductCategory.ELECTRONICS, ProductStatus.ACTIVE, null
        );
        ProductResponseDto response = new ProductResponseDto(
                1L, "Gaming Laptop", "High performance", new BigDecimal("1499.99"), 15,
                ProductCategory.ELECTRONICS, ProductStatus.ACTIVE, null,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(productService.create(any(ProductRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Gaming Laptop"));
    }

    @Test
    @DisplayName("GET /api/v1/products/{id} - Success")
    void getById_Success() throws Exception {
        ProductResponseDto response = new ProductResponseDto(
                1L, "Gaming Laptop", "High performance", new BigDecimal("1499.99"), 15,
                ProductCategory.ELECTRONICS, ProductStatus.ACTIVE, null,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(productService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gaming Laptop"));
    }

    @Test
    @DisplayName("POST /api/v1/products/{id}/reduce-stock - Success")
    void reduceStock_Success() throws Exception {
        ProductResponseDto response = new ProductResponseDto(
                1L, "Gaming Laptop", "High performance", new BigDecimal("1499.99"), 10,
                ProductCategory.ELECTRONICS, ProductStatus.ACTIVE, null,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(productService.reduceStock(1L, 5)).thenReturn(response);

        mockMvc.perform(post("/api/v1/products/1/reduce-stock")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(10));
    }
}
