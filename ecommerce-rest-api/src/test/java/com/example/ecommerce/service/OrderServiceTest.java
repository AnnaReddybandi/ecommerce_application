package com.example.ecommerce.service;

import com.example.ecommerce.dto.order.CheckoutRequestDto;
import com.example.ecommerce.dto.order.OrderRequestDto;
import com.example.ecommerce.dto.order.OrderResponseDto;
import com.example.ecommerce.dto.orderitem.OrderItemRequestDto;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.entity.enums.OrderStatus;
import com.example.ecommerce.entity.enums.PaymentMethod;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.InvalidOrderException;
import com.example.ecommerce.repository.*;
import com.example.ecommerce.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Customer customer;
    private Product product;
    private Order order;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setName("Jane Doe");
        customer.setEmail("jane@example.com");

        product = new Product();
        product.setId(10L);
        product.setName("Laptop");
        product.setPrice(new BigDecimal("1000.00"));
        product.setStock(20);
        product.setStatus(ProductStatus.ACTIVE);

        order = new Order();
        order.setId(100L);
        order.setCustomer(customer);
        order.setShippingAddress("456 Elm St");
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("2000.00"));
        order.setOrderDate(LocalDateTime.now());
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setOrderItems(new ArrayList<>());
    }

    @Test
    @DisplayName("Create order successfully")
    void createOrder_Success() {
        OrderItemRequestDto itemDto = new OrderItemRequestDto(null, 10L, 2);
        OrderRequestDto requestDto = new OrderRequestDto(1L, "456 Elm St", List.of(itemDto));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(100L);
            o.setCreatedAt(LocalDateTime.now());
            o.setUpdatedAt(LocalDateTime.now());
            return o;
        });

        OrderResponseDto response = orderService.create(requestDto);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(100L);
        assertThat(product.getStock()).isEqualTo(18); // Reduced stock
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("Create order with insufficient stock throws InsufficientStockException")
    void createOrder_InsufficientStock_ThrowsException() {
        OrderItemRequestDto itemDto = new OrderItemRequestDto(null, 10L, 50);
        OrderRequestDto requestDto = new OrderRequestDto(1L, "456 Elm St", List.of(itemDto));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> orderService.create(requestDto))
                .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    @DisplayName("Confirm pending order successfully")
    void confirmOrder_Success() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponseDto response = orderService.confirmOrder(100L);

        assertThat(response).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Confirm non-pending order throws InvalidOrderException")
    void confirmOrder_NonPending_ThrowsException() {
        order.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.confirmOrder(100L))
                .isInstanceOf(InvalidOrderException.class);
    }

    @Test
    @DisplayName("Cancel order successfully")
    void cancelOrder_Success() {
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponseDto response = orderService.cancelOrder(100L);

        assertThat(response).isNotNull();
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("Cancel delivered order throws InvalidOrderException")
    void cancelOrder_Delivered_ThrowsException() {
        order.setStatus(OrderStatus.DELIVERED);
        when(orderRepository.findById(100L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(100L))
                .isInstanceOf(InvalidOrderException.class);
    }

    @Test
    @DisplayName("Checkout shopping cart successfully")
    void checkout_Success() {
        ShoppingCart cart = new ShoppingCart();
        cart.setId(5L);
        cart.setCustomer(customer);

        CartItem cartItem = new CartItem(cart, product, 2);
        cartItem.setId(50L);
        cart.setCartItems(new ArrayList<>(List.of(cartItem)));

        CheckoutRequestDto checkoutDto = new CheckoutRequestDto(
                1L, "456 Elm St", PaymentMethod.CARD, "Fast delivery"
        );

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(shoppingCartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByShoppingCartId(5L)).thenReturn(List.of(cartItem));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setId(100L);
            o.setCreatedAt(LocalDateTime.now());
            o.setUpdatedAt(LocalDateTime.now());
            return o;
        });
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> {
            Payment p = invocation.getArgument(0);
            p.setId(200L);
            p.setCreatedAt(LocalDateTime.now());
            p.setUpdatedAt(LocalDateTime.now());
            return p;
        });

        OrderResponseDto response = orderService.checkout(checkoutDto);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(100L);
        verify(cartItemRepository).deleteByShoppingCartId(5L);
    }
}
