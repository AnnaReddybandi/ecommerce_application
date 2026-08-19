package com.example.ecommerce.service;

import com.example.ecommerce.dto.orderitem.OrderItemRequestDto;
import com.example.ecommerce.dto.orderitem.OrderItemResponseDto;

import java.util.List;

public interface OrderItemService {

    // Create
    OrderItemResponseDto create(
            OrderItemRequestDto request
    );

    // Get by ID
    OrderItemResponseDto getById(
            Long id
    );

    // Get all
    List<OrderItemResponseDto> getAll();

    // Update
    OrderItemResponseDto update(
            Long id,
            OrderItemRequestDto request
    );

    // Delete
    void delete(
            Long id
    );

    // Get by order ID
    List<OrderItemResponseDto> getByOrderId(
            Long orderId
    );
}