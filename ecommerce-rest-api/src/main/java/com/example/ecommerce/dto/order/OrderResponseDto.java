package com.example.ecommerce.dto.order;

import com.example.ecommerce.dto.orderitem.OrderItemResponseDto;
import com.example.ecommerce.dto.payment.PaymentResponseDto;
import com.example.ecommerce.entity.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDto(

        Long id,

        Long customerId,

        String customerName,

        LocalDateTime orderDate,

        OrderStatus status,

        String shippingAddress,

        BigDecimal totalAmount,

        List<OrderItemResponseDto> orderItems,

        PaymentResponseDto payment,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}