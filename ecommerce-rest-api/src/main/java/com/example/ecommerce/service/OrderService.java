package com.example.ecommerce.service;

import com.example.ecommerce.dto.order.CheckoutRequestDto;
import com.example.ecommerce.dto.order.OrderRequestDto;
import com.example.ecommerce.dto.order.OrderResponseDto;
import com.example.ecommerce.entity.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderResponseDto create(OrderRequestDto request);

    OrderResponseDto getById(Long id);

    List<OrderResponseDto> getAll();

    OrderResponseDto update(
            Long id,
            OrderRequestDto request
    );

    void delete(Long id);

    OrderResponseDto confirmOrder(Long id);

    OrderResponseDto cancelOrder(Long id);

    OrderResponseDto checkout(
            CheckoutRequestDto request
    );

    List<OrderResponseDto> getByStatus(
            OrderStatus status
    );

    List<OrderResponseDto> getByCustomerId(
            Long customerId
    );
}