package com.example.ecommerce.controller;

import com.example.ecommerce.controller.v1.CustomerController;
import com.example.ecommerce.dto.customer.CustomerRequestDto;
import com.example.ecommerce.dto.customer.CustomerResponseDto;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    @Test
    @DisplayName("POST /api/v1/customers - Create customer success")
    void createCustomer_Success() throws Exception {
        CustomerRequestDto request = new CustomerRequestDto(
                "Alice", "alice@example.com", "9876543210", "456 Oak Avenue"
        );
        CustomerResponseDto response = new CustomerResponseDto(
                1L, "Alice", "alice@example.com", "9876543210", "456 Oak Avenue",
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(customerService.create(any(CustomerRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    @DisplayName("POST /api/v1/customers - Validation failure returns 400")
    void createCustomer_ValidationFailure() throws Exception {
        CustomerRequestDto invalidRequest = new CustomerRequestDto(
                "", "invalid-email", "123", ""
        );

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    @DisplayName("GET /api/v1/customers/{id} - Success")
    void getById_Success() throws Exception {
        CustomerResponseDto response = new CustomerResponseDto(
                1L, "Alice", "alice@example.com", "9876543210", "456 Oak Avenue",
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(customerService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    @DisplayName("GET /api/v1/customers/{id} - Not Found returns 404")
    void getById_NotFound() throws Exception {
        when(customerService.getById(99L)).thenThrow(new ResourceNotFoundException("Customer not found with ID: 99"));

        mockMvc.perform(get("/api/v1/customers/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Customer not found with ID: 99"));
    }

    @Test
    @DisplayName("GET /api/v1/customers - Get all customers")
    void getAll_Success() throws Exception {
        CustomerResponseDto response = new CustomerResponseDto(
                1L, "Alice", "alice@example.com", "9876543210", "456 Oak Avenue",
                LocalDateTime.now(), LocalDateTime.now()
        );

        when(customerService.getAll()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("DELETE /api/v1/customers/{id} - Success")
    void delete_Success() throws Exception {
        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpect(status().isNoContent());
    }
}
