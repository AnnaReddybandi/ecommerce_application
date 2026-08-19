package com.example.ecommerce.controller;

import com.example.ecommerce.controller.v1.PaymentController;
import com.example.ecommerce.dto.payment.PaymentRequestDto;
import com.example.ecommerce.dto.payment.PaymentResponseDto;
import com.example.ecommerce.entity.enums.PaymentMethod;
import com.example.ecommerce.entity.enums.PaymentStatus;
import com.example.ecommerce.service.PaymentService;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    @DisplayName("POST /api/v1/payments - Create payment success")
    void createPayment_Success() throws Exception {
        PaymentRequestDto request = new PaymentRequestDto(
                1L, new BigDecimal("199.99"), PaymentMethod.CARD, "TXN-999", "Fast checkout"
        );
        PaymentResponseDto response = new PaymentResponseDto(
                10L, 1L, LocalDateTime.now(), new BigDecimal("199.99"),
                PaymentMethod.CARD, PaymentStatus.PENDING, "TXN-999", "Fast checkout",
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(paymentService.create(any(PaymentRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /api/v1/payments/{id}/process - Process payment success")
    void processPayment_Success() throws Exception {
        PaymentResponseDto response = new PaymentResponseDto(
                10L, 1L, LocalDateTime.now(), new BigDecimal("199.99"),
                PaymentMethod.CARD, PaymentStatus.SUCCESS, "TXN-999", "Fast checkout",
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(paymentService.processPayment(10L)).thenReturn(response);

        mockMvc.perform(post("/api/v1/payments/10/process"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}
