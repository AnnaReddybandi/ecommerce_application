package com.example.ecommerce.service;

import com.example.ecommerce.dto.payment.PaymentRequestDto;
import com.example.ecommerce.dto.payment.PaymentResponseDto;
import com.example.ecommerce.entity.*;
import com.example.ecommerce.entity.enums.OrderStatus;
import com.example.ecommerce.entity.enums.PaymentMethod;
import com.example.ecommerce.entity.enums.PaymentStatus;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.PaymentRepository;
import com.example.ecommerce.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Order order;
    private Payment payment;

    @BeforeEach
    void setUp() {
        Customer customer = new Customer();
        customer.setId(1L);

        order = new Order();
        order.setId(10L);
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("500.00"));

        payment = new Payment();
        payment.setId(100L);
        payment.setOrder(order);
        payment.setAmount(new BigDecimal("500.00"));
        payment.setMethod(PaymentMethod.CARD);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Create payment successfully")
    void createPayment_Success() {
        PaymentRequestDto request = new PaymentRequestDto(
                10L, new BigDecimal("500.00"), PaymentMethod.CARD, "TXN-123", "Initial payment"
        );

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(10L)).thenReturn(Optional.empty());
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentResponseDto response = paymentService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.method()).isEqualTo(PaymentMethod.CARD);
    }

    @Test
    @DisplayName("Create duplicate payment throws IllegalStateException")
    void createPayment_Duplicate_ThrowsException() {
        PaymentRequestDto request = new PaymentRequestDto(
                10L, new BigDecimal("500.00"), PaymentMethod.CARD, "TXN-123", "Duplicate"
        );

        when(orderRepository.findById(10L)).thenReturn(Optional.of(order));
        when(paymentRepository.findByOrderId(10L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.create(request))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Payment already exists");
    }

    @Test
    @DisplayName("Process pending payment successfully confirms order")
    void processPayment_Success() {
        when(paymentRepository.findById(100L)).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        PaymentResponseDto response = paymentService.processPayment(100L);

        assertThat(response).isNotNull();
        assertThat(payment.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Process non-pending payment throws IllegalStateException")
    void processPayment_NonPending_ThrowsException() {
        payment.setStatus(PaymentStatus.SUCCESS);
        when(paymentRepository.findById(100L)).thenReturn(Optional.of(payment));

        assertThatThrownBy(() -> paymentService.processPayment(100L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only pending payments can be processed");
    }

    @Test
    @DisplayName("Get payment by order ID")
    void getByOrderId_Success() {
        when(paymentRepository.findByOrderId(10L)).thenReturn(Optional.of(payment));

        PaymentResponseDto response = paymentService.getByOrderId(10L);

        assertThat(response).isNotNull();
        assertThat(response.orderId()).isEqualTo(10L);
    }
}
