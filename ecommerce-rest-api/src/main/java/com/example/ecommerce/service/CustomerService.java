package com.example.ecommerce.service;

import com.example.ecommerce.dto.customer.CustomerRequestDto;
import com.example.ecommerce.dto.customer.CustomerResponseDto;

import java.util.List;

public interface CustomerService {

    CustomerResponseDto create(CustomerRequestDto request);

    List<CustomerResponseDto> getAll();

    CustomerResponseDto getById(Long id);

    CustomerResponseDto update(Long id, CustomerRequestDto request);

    void delete(Long id);
}