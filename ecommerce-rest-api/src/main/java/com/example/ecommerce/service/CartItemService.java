package com.example.ecommerce.service;

import com.example.ecommerce.dto.cartitem.CartItemRequestDto;
import com.example.ecommerce.dto.cartitem.CartItemResponseDto;

import java.util.List;

public interface CartItemService {

    /**
     * Add a product to a shopping cart.
     */
    CartItemResponseDto create(CartItemRequestDto request);

    /**
     * Get all cart items.
     */
    List<CartItemResponseDto> getAll();

    /**
     * Get cart item by ID.
     */
    CartItemResponseDto getById(Long id);

    /**
     * Get all items belonging to a cart.
     */
    List<CartItemResponseDto> getByCartId(Long cartId);

    /**
     * Update cart item quantity.
     */
    CartItemResponseDto update(
            Long id,
            CartItemRequestDto request
    );

    /**
     * Delete cart item.
     */
    void delete(Long id);
}