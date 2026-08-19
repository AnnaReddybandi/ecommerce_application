package com.example.ecommerce.service;

import com.example.ecommerce.dto.orderitem.OrderItemRequestDto;
import com.example.ecommerce.dto.orderitem.OrderItemResponseDto;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.impl.OrderItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    private Order order;
    private Product product;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        Customer customer = new Customer();
        customer.setId(1L);

        order = new Order();
        order.setId(5L);
        order.setCustomer(customer);

        product = new Product();
        product.setId(15L);
        product.setName("Keyboard");
        product.setPrice(new BigDecimal("75.00"));

        orderItem = new OrderItem();
        orderItem.setId(50L);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("75.00"));
        orderItem.setCreatedAt(LocalDateTime.now());
        orderItem.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Create order item successfully")
    void createOrderItem_Success() {
        OrderItemRequestDto request = new OrderItemRequestDto(5L, 15L, 2);

        when(orderRepository.findById(5L)).thenReturn(Optional.of(order));
        when(productRepository.findById(15L)).thenReturn(Optional.of(product));
        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);

        OrderItemResponseDto response = orderItemService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(50L);
        assertThat(response.subtotal()).isEqualByComparingTo(new BigDecimal("150.00"));
    }

    @Test
    @DisplayName("Get order item by ID successfully")
    void getById_Success() {
        when(orderItemRepository.findById(50L)).thenReturn(Optional.of(orderItem));

        OrderItemResponseDto response = orderItemService.getById(50L);

        assertThat(response).isNotNull();
        assertThat(response.productName()).isEqualTo("Keyboard");
    }

    @Test
    @DisplayName("Get order items by order ID")
    void getByOrderId_Success() {
        when(orderRepository.findById(5L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderId(5L)).thenReturn(List.of(orderItem));

        List<OrderItemResponseDto> list = orderItemService.getByOrderId(5L);

        assertThat(list).hasSize(1);
    }
}
