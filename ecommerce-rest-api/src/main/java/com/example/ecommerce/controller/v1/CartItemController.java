package com.example.ecommerce.controller.v1;

import com.example.ecommerce.dto.cartitem.CartItemRequestDto;
import com.example.ecommerce.dto.cartitem.CartItemResponseDto;
import com.example.ecommerce.service.CartItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cart-items")
@Tag(
        name = "Cart Items",
        description = "Cart Item Management APIs"
)
public class CartItemController {

    private final CartItemService cartItemService;

    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }


    //post : http://localhost:8080/api/v1/cart-items
    @PostMapping
    @Operation(summary = "Add an item to shopping cart")
    public ResponseEntity<CartItemResponseDto> create(
            @Valid @RequestBody CartItemRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(cartItemService.create(request));
    }

    @GetMapping
    @Operation(summary = "Get all cart items")
    public ResponseEntity<List<CartItemResponseDto>> getAll() {

        return ResponseEntity.ok(
                cartItemService.getAll()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get cart item by ID")
    public ResponseEntity<CartItemResponseDto> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                cartItemService.getById(id)
        );
    }

    @GetMapping("/cart/{cartId}")
    @Operation(summary = "Get items in cart by cart ID")
    public ResponseEntity<List<CartItemResponseDto>> getByCartId(
            @PathVariable Long cartId) {

        return ResponseEntity.ok(
                cartItemService.getByCartId(cartId)
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update cart item quantity")
    public ResponseEntity<CartItemResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody CartItemRequestDto request) {

        return ResponseEntity.ok(
                cartItemService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete cart item by ID")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        cartItemService.delete(id);

        return ResponseEntity.noContent().build();
    }
}