package com.example.ecommerce.service;

import com.example.ecommerce.dto.cart.ShoppingCartRequestDto;
import com.example.ecommerce.dto.cart.ShoppingCartResponseDto;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.ShoppingCart;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.ShoppingCartRepository;
import com.example.ecommerce.service.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceTest {

    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    private Customer customer;
    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);

        cart = new ShoppingCart();
        cart.setId(10L);
        cart.setCustomer(customer);
        cart.setCartItems(new ArrayList<>());
        cart.setCreatedAt(LocalDateTime.now());
        cart.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Create shopping cart successfully")
    void createCart_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(cart);

        ShoppingCartResponseDto response = shoppingCartService.create(new ShoppingCartRequestDto(1L));

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.customerId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Get shopping cart by ID successfully")
    void getById_Success() {
        when(shoppingCartRepository.findById(10L)).thenReturn(Optional.of(cart));

        ShoppingCartResponseDto response = shoppingCartService.getById(10L);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Clear shopping cart successfully")
    void clearCart_Success() {
        when(shoppingCartRepository.findById(10L)).thenReturn(Optional.of(cart));

        ShoppingCartResponseDto response = shoppingCartService.clearCart(10L);

        assertThat(response).isNotNull();
        verify(cartItemRepository).deleteByShoppingCartId(10L);
    }

    @Test
    @DisplayName("Delete shopping cart successfully")
    void deleteCart_Success() {
        when(shoppingCartRepository.findById(10L)).thenReturn(Optional.of(cart));

        shoppingCartService.delete(10L);

        verify(cartItemRepository).deleteByShoppingCartId(10L);
        verify(shoppingCartRepository).delete(cart);
    }
}
