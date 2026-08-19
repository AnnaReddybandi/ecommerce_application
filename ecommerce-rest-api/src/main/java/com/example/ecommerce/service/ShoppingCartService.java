package com.example.ecommerce.service;

import com.example.ecommerce.dto.cart.ShoppingCartRequestDto;
import com.example.ecommerce.dto.cart.ShoppingCartResponseDto;

import java.util.List;

public interface ShoppingCartService {

    // Create cart
    ShoppingCartResponseDto create(
            ShoppingCartRequestDto request
    );

    // Get cart by ID
    ShoppingCartResponseDto getById(
            Long id
    );

    // Get all carts
    List<ShoppingCartResponseDto> getAll();

    // Get cart by customer
    ShoppingCartResponseDto getByCustomerId(
            Long customerId
    );

    // Delete cart
    void delete(
            Long id
    );

    // Clear cart
    ShoppingCartResponseDto clearCart(
            Long id
    );

    // Find abandoned carts
    List<ShoppingCartResponseDto> findAbandonedCarts(
            int hours
    );
}