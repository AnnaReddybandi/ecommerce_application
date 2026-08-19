package com.example.ecommerce.controller.v1;

import com.example.ecommerce.dto.order.OrderRequestDto;
import com.example.ecommerce.dto.order.OrderResponseDto;
import com.example.ecommerce.dto.order.CheckoutRequestDto;
import com.example.ecommerce.entity.enums.OrderStatus;
import com.example.ecommerce.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(
        name = "Orders",
        description = "Order Management APIs"
)
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    @Operation(summary = "Create a new order")
    public ResponseEntity<OrderResponseDto> create(
            @Valid @RequestBody OrderRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.create(request));
    }

    @GetMapping
    @Operation(summary = "Get all orders")
    public ResponseEntity<List<OrderResponseDto>> getAll() {

        return ResponseEntity.ok(
                orderService.getAll()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderResponseDto> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                orderService.getById(id)
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update order details")
    public ResponseEntity<OrderResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody OrderRequestDto request) {

        return ResponseEntity.ok(
                orderService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order by ID")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        orderService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/confirm")
    @Operation(summary = "Confirm an order")
    public ResponseEntity<OrderResponseDto> confirmOrder(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                orderService.confirmOrder(id)
        );
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel an order")
    public ResponseEntity<OrderResponseDto> cancelOrder(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                orderService.cancelOrder(id)
        );
    }

    @PostMapping("/checkout")
    @Operation(
            summary = "Checkout shopping cart",
            description = "Checkout shopping cart and create order transactionally"
    )
    public ResponseEntity<OrderResponseDto> checkout(
            @Valid @RequestBody CheckoutRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.checkout(request));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get orders by order status")
    public ResponseEntity<List<OrderResponseDto>> getByStatus(
            @PathVariable OrderStatus status) {

        return ResponseEntity.ok(
                orderService.getByStatus(status)
        );
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get orders by customer ID")
    public ResponseEntity<List<OrderResponseDto>> getByCustomerId(
            @PathVariable Long customerId) {

        return ResponseEntity.ok(
                orderService.getByCustomerId(customerId)
        );
    }
}