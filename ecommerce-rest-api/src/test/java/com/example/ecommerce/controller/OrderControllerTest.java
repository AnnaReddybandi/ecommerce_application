package com.example.ecommerce.controller;

import com.example.ecommerce.controller.v1.OrderController;
import com.example.ecommerce.dto.order.CheckoutRequestDto;
import com.example.ecommerce.dto.order.OrderRequestDto;
import com.example.ecommerce.dto.order.OrderResponseDto;
import com.example.ecommerce.dto.orderitem.OrderItemRequestDto;
import com.example.ecommerce.entity.enums.OrderStatus;
import com.example.ecommerce.entity.enums.PaymentMethod;
import com.example.ecommerce.service.OrderService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @Test
    @DisplayName("POST /api/v1/orders - Create order success")
    void createOrder_Success() throws Exception {
        OrderItemRequestDto itemDto = new OrderItemRequestDto(1L, 1L, 2);
        OrderRequestDto request = new OrderRequestDto(1L, "123 Main St", List.of(itemDto));

        OrderResponseDto response = new OrderResponseDto(
                10L, 1L, "John Doe", LocalDateTime.now(), OrderStatus.PENDING,
                "123 Main St", new BigDecimal("500.00"), List.of(), null,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(orderService.create(any(OrderRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /api/v1/orders/{id}/confirm - Confirm order success")
    void confirmOrder_Success() throws Exception {
        OrderResponseDto response = new OrderResponseDto(
                10L, 1L, "John Doe", LocalDateTime.now(), OrderStatus.CONFIRMED,
                "123 Main St", new BigDecimal("500.00"), List.of(), null,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(orderService.confirmOrder(10L)).thenReturn(response);

        mockMvc.perform(post("/api/v1/orders/10/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("POST /api/v1/orders/{id}/cancel - Cancel order success")
    void cancelOrder_Success() throws Exception {
        OrderResponseDto response = new OrderResponseDto(
                10L, 1L, "John Doe", LocalDateTime.now(), OrderStatus.CANCELLED,
                "123 Main St", new BigDecimal("500.00"), List.of(), null,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(orderService.cancelOrder(10L)).thenReturn(response);

        mockMvc.perform(post("/api/v1/orders/10/cancel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @DisplayName("POST /api/v1/orders/checkout - Checkout success")
    void checkout_Success() throws Exception {
        CheckoutRequestDto request = new CheckoutRequestDto(
                1L, "123 Main St", PaymentMethod.UPI, "Express delivery"
        );
        OrderResponseDto response = new OrderResponseDto(
                10L, 1L, "John Doe", LocalDateTime.now(), OrderStatus.PENDING,
                "123 Main St", new BigDecimal("500.00"), List.of(), null,
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(orderService.checkout(any(CheckoutRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/orders/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L));
    }
}
