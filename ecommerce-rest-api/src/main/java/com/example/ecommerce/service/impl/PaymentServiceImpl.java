package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.payment.PaymentRequestDto;
import com.example.ecommerce.dto.payment.PaymentResponseDto;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.enums.OrderStatus;
import com.example.ecommerce.entity.Payment;
import com.example.ecommerce.entity.enums.PaymentStatus;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.PaymentRepository;
import com.example.ecommerce.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * PaymentServiceImpl
 *
 * Implementation of PaymentService.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    // ============================================================
    // CREATE PAYMENT
    // ============================================================

    @Override
    public PaymentResponseDto create(
            PaymentRequestDto request) {

        log.info(
                "Creating payment for order ID: {}",
                request.orderId()
        );

        Order order =
                orderRepository.findById(
                                request.orderId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found with ID: "
                                                + request.orderId()
                                )
                        );

        if (paymentRepository.findByOrderId(
                request.orderId()
        ).isPresent()) {

            throw new IllegalStateException(
                    "Payment already exists for order ID: "
                            + request.orderId()
            );
        }

        Payment payment = new Payment();

        payment.setOrder(order);
        payment.setAmount(request.amount());
        payment.setMethod(request.method());
        payment.setTransactionId(
                request.transactionId()
        );
        payment.setNotes(
                request.notes()
        );
        payment.setPaymentDate(
                LocalDateTime.now()
        );
        payment.setStatus(
                PaymentStatus.PENDING
        );

        Payment saved =
                paymentRepository.save(payment);

        log.info(
                "Payment created successfully with ID: {}",
                saved.getId()
        );

        return mapToResponse(saved);
    }

    // ============================================================
    // GET BY ID
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getById(Long id) {

        Payment payment =
                paymentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found with ID: "
                                                + id
                                )
                        );

        return mapToResponse(payment);
    }

    // ============================================================
    // GET ALL
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getAll() {

        return paymentRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ============================================================
    // UPDATE
    // ============================================================

    @Override
    public PaymentResponseDto update(
            Long id,
            PaymentRequestDto request) {

        Payment payment =
                paymentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found with ID: "
                                                + id
                                )
                        );

        Order order =
                orderRepository.findById(
                                request.orderId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found with ID: "
                                                + request.orderId()
                                )
                        );

        payment.setOrder(order);
        payment.setAmount(request.amount());
        payment.setMethod(request.method());
        payment.setTransactionId(
                request.transactionId()
        );
        payment.setNotes(
                request.notes()
        );

        Payment updated =
                paymentRepository.save(payment);

        return mapToResponse(updated);
    }

    // ============================================================
    // DELETE
    // ============================================================

    @Override
    public void delete(Long id) {

        Payment payment =
                paymentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found with ID: "
                                                + id
                                )
                        );

        paymentRepository.delete(payment);
    }

    // ============================================================
    // PROCESS PAYMENT
    // ============================================================

    @Override
    public PaymentResponseDto processPayment(Long id) {

        log.info(
                "Processing payment with ID: {}",
                id
        );

        Payment payment =
                paymentRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found with ID: "
                                                + id
                                )
                        );

        if (payment.getStatus() != PaymentStatus.PENDING) {

            throw new IllegalStateException(
                    "Only pending payments can be processed"
            );
        }

        /*
         * Demo payment processing.
         *
         * In a real application this is where
         * Razorpay / Stripe / PayPal integration
         * would be performed.
         */

        if (payment.getTransactionId() == null
                || payment.getTransactionId().isBlank()) {

            payment.setTransactionId(
                    "TXN-" + UUID.randomUUID()
            );
        }

        payment.setStatus(
                PaymentStatus.SUCCESS
        );

        Order order =
                payment.getOrder();

        order.setStatus(
                OrderStatus.CONFIRMED
        );

        orderRepository.save(order);

        Payment processed =
                paymentRepository.save(payment);

        log.info(
                "Payment processed successfully with ID: {}",
                id
        );

        return mapToResponse(processed);
    }

    // ============================================================
    // GET BY STATUS
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getByStatus(
            PaymentStatus status) {

        return paymentRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ============================================================
    // GET BY ORDER ID
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getByOrderId(
            Long orderId) {

        Payment payment =
                paymentRepository.findByOrderId(orderId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Payment not found for order ID: "
                                                + orderId
                                )
                        );

        return mapToResponse(payment);
    }

    // ============================================================
    // ENTITY -> DTO
    // ============================================================

    private PaymentResponseDto mapToResponse(
            Payment payment) {

        return new PaymentResponseDto(
                payment.getId(),
                payment.getOrder().getId(),
                payment.getPaymentDate(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getTransactionId(),
                payment.getNotes(),
                payment.getCreatedAt(),
                payment.getUpdatedAt()
        );
    }
}