package com.example.ecommerce.controller.v1;

import com.example.ecommerce.dto.orderitem.OrderItemRequestDto;
import com.example.ecommerce.dto.orderitem.OrderItemResponseDto;
import com.example.ecommerce.service.OrderItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order-items")
@Tag(
        name = "Order Items",
        description = "Order Item Management APIs"
)
public class OrderItemController {

    private final OrderItemService orderItemService;

    public OrderItemController(OrderItemService orderItemService) {
        this.orderItemService = orderItemService;
    }

    @PostMapping
    @Operation(summary = "Create a new order item")
    public ResponseEntity<OrderItemResponseDto> create(
            @Valid @RequestBody OrderItemRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderItemService.create(request));
    }

    @GetMapping
    @Operation(summary = "Get all order items")
    public ResponseEntity<List<OrderItemResponseDto>> getAll() {

        return ResponseEntity.ok(
                orderItemService.getAll()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order item by ID")
    public ResponseEntity<OrderItemResponseDto> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                orderItemService.getById(id)
        );
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get order items by order ID")
    public ResponseEntity<List<OrderItemResponseDto>> getByOrderId(
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                orderItemService.getByOrderId(orderId)
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update order item details")
    public ResponseEntity<OrderItemResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody OrderItemRequestDto request) {

        return ResponseEntity.ok(
                orderItemService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order item by ID")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        orderItemService.delete(id);

        return ResponseEntity.noContent().build();
    }
}