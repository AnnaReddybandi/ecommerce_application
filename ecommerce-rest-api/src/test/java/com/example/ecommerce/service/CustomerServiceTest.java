package com.example.ecommerce.service;

import com.example.ecommerce.dto.customer.CustomerRequestDto;
import com.example.ecommerce.dto.customer.CustomerResponseDto;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.exception.DuplicateResourceException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerRequestDto requestDto;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setPhone("1234567890");
        customer.setAddress("123 Main St");
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());

        requestDto = new CustomerRequestDto(
                "John Doe",
                "john@example.com",
                "1234567890",
                "123 Main St"
        );
    }

    @Test
    @DisplayName("Create customer successfully")
    void createCustomer_Success() {
        when(customerRepository.findByEmail(requestDto.email())).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerResponseDto response = customerService.create(requestDto);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.email()).isEqualTo("john@example.com");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    @DisplayName("Create customer with duplicate email throws DuplicateResourceException")
    void createCustomer_DuplicateEmail_ThrowsException() {
        when(customerRepository.findByEmail(requestDto.email())).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> customerService.create(requestDto))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("Customer already exists with email");

        verify(customerRepository, never()).save(any());
    }

    @Test
    @DisplayName("Get customer by ID successfully")
    void getById_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        CustomerResponseDto response = customerService.getById(1L);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("Get customer by non-existent ID throws ResourceNotFoundException")
    void getById_NotFound_ThrowsException() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found with ID");
    }

    @Test
    @DisplayName("Get all customers returns list")
    void getAll_Success() {
        when(customerRepository.findAll()).thenReturn(List.of(customer));

        List<CustomerResponseDto> list = customerService.getAll();

        assertThat(list).hasSize(1);
        assertThat(list.get(0).email()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("Update customer successfully")
    void updateCustomer_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.findByEmail(requestDto.email())).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        CustomerResponseDto response = customerService.update(1L, requestDto);

        assertThat(response).isNotNull();
        verify(customerRepository).save(customer);
    }

    @Test
    @DisplayName("Delete customer successfully")
    void deleteCustomer_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).delete(customer);

        customerService.delete(1L);

        verify(customerRepository).delete(customer);
    }
}
