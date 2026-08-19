package com.example.ecommerce.controller.v1;

import com.example.ecommerce.dto.payment.PaymentRequestDto;
import com.example.ecommerce.dto.payment.PaymentResponseDto;
import com.example.ecommerce.entity.enums.PaymentStatus;
import com.example.ecommerce.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(
        name = "Payments",
        description = "Payment Management APIs"
)
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    @Operation(summary = "Create a payment record")
    public ResponseEntity<PaymentResponseDto> create(
            @Valid @RequestBody PaymentRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentService.create(request));
    }

    @GetMapping
    @Operation(summary = "Get all payments")
    public ResponseEntity<List<PaymentResponseDto>> getAll() {

        return ResponseEntity.ok(
                paymentService.getAll()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<PaymentResponseDto> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                paymentService.getById(id)
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payment details")
    public ResponseEntity<PaymentResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody PaymentRequestDto request) {

        return ResponseEntity.ok(
                paymentService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment by ID")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        paymentService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/process")
    @Operation(
            summary = "Process payment",
            description = "Process payment and confirm associated order"
    )
    public ResponseEntity<PaymentResponseDto> processPayment(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                paymentService.processPayment(id)
        );
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get payments by status")
    public ResponseEntity<List<PaymentResponseDto>> getByStatus(
            @PathVariable PaymentStatus status) {

        return ResponseEntity.ok(
                paymentService.getByStatus(status)
        );
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by order ID")
    public ResponseEntity<PaymentResponseDto> getByOrderId(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                paymentService.getByOrderId(orderId)
        );
    }
}