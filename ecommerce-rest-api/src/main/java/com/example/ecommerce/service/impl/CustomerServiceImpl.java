package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.customer.CustomerRequestDto;
import com.example.ecommerce.dto.customer.CustomerResponseDto;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.exception.DuplicateResourceException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.service.CustomerService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    // ============================================================
    // CREATE
    // ============================================================

    @Override
    public CustomerResponseDto create(CustomerRequestDto request) {

        log.info("Creating customer with email: {}", request.email());

        if (customerRepository.findByEmail(request.email()).isPresent()) {
            throw new DuplicateResourceException(
                    "Customer already exists with email: " + request.email()
            );
        }

        Customer customer = new Customer();

        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        customer.setAddress(request.address());

        Customer savedCustomer = customerRepository.save(customer);

        log.info(
                "Customer created successfully with ID: {}",
                savedCustomer.getId()
        );

        return mapToResponse(savedCustomer);
    }

    // ============================================================
    // GET ALL
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDto> getAll() {

        log.info("Fetching all customers");

        return customerRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ============================================================
    // GET BY ID
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDto getById(Long id) {

        log.info("Fetching customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with ID: " + id
                        )
                );

        return mapToResponse(customer);
    }

    // ============================================================
    // UPDATE
    // ============================================================

    @Override
    public CustomerResponseDto update(
            Long id,
            CustomerRequestDto request) {

        log.info("Updating customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with ID: " + id
                        )
                );

        customerRepository.findByEmail(request.email())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new DuplicateResourceException(
                                "Email already exists: " + request.email()
                        );
                    }
                });

        customer.setName(request.name());
        customer.setEmail(request.email());
        customer.setPhone(request.phone());
        customer.setAddress(request.address());

        Customer updatedCustomer =
                customerRepository.save(customer);

        log.info(
                "Customer updated successfully with ID: {}",
                id
        );

        return mapToResponse(updatedCustomer);
    }

    // ============================================================
    // DELETE
    // ============================================================

    @Override
    public void delete(Long id) {

        log.info("Deleting customer with ID: {}", id);

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with ID: " + id
                        )
                );

        customerRepository.delete(customer);

        log.info(
                "Customer deleted successfully with ID: {}",
                id
        );
    }

    // ============================================================
    // ENTITY -> DTO
    // ============================================================

    private CustomerResponseDto mapToResponse(Customer customer) {

        return new CustomerResponseDto(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}