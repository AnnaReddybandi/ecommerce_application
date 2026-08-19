package com.example.ecommerce.controller.v1;

import com.example.ecommerce.dto.cart.ShoppingCartRequestDto;
import com.example.ecommerce.dto.cart.ShoppingCartResponseDto;
import com.example.ecommerce.service.ShoppingCartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shopping-carts")
@Tag(
        name = "Shopping Carts",
        description = "Shopping Cart Management APIs"
)
public class ShoppingCartController {

    private final ShoppingCartService shoppingCartService;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public ShoppingCartController(
            ShoppingCartService shoppingCartService) {

        this.shoppingCartService = shoppingCartService;
    }

    // ============================================================
    // CREATE SHOPPING CART
    // ============================================================

    @PostMapping
    @Operation(
            summary = "Create a shopping cart",
            description = "Creates a new shopping cart for a customer"
    )
    public ResponseEntity<ShoppingCartResponseDto> create(
            @Valid
            @RequestBody
            ShoppingCartRequestDto request) {

        ShoppingCartResponseDto response =
                shoppingCartService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ============================================================
    // GET ALL SHOPPING CARTS
    // ============================================================

    @GetMapping
    @Operation(
            summary = "Get all shopping carts",
            description = "Returns all shopping carts"
    )
    public ResponseEntity<List<ShoppingCartResponseDto>> getAll() {

        List<ShoppingCartResponseDto> response =
                shoppingCartService.getAll();

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // GET SHOPPING CART BY ID
    // ============================================================

    @GetMapping("/{id}")
    @Operation(
            summary = "Get shopping cart by ID",
            description = "Returns a shopping cart using its ID"
    )
    public ResponseEntity<ShoppingCartResponseDto> getById(
            @PathVariable Long id) {

        ShoppingCartResponseDto response =
                shoppingCartService.getById(id);

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // GET SHOPPING CART BY CUSTOMER ID
    // ============================================================

    @GetMapping("/customer/{customerId}")
    @Operation(
            summary = "Get shopping cart by customer ID",
            description = "Returns the shopping cart belonging to a customer"
    )
    public ResponseEntity<ShoppingCartResponseDto> getByCustomerId(
            @PathVariable Long customerId) {

        ShoppingCartResponseDto response =
                shoppingCartService.getByCustomerId(customerId);

        return ResponseEntity.ok(response);
    }

    // ============================================================
    // DELETE SHOPPING CART
    // ============================================================

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete shopping cart",
            description = "Deletes a shopping cart using its ID"
    )
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        shoppingCartService.delete(id);

        return ResponseEntity
                .noContent()
                .build();
    }

    // ============================================================
    // CLEAR SHOPPING CART
    // ============================================================

    @DeleteMapping("/{id}/clear")
    @Operation(
            summary = "Clear shopping cart",
            description = "Removes all items from a shopping cart"
    )
    public ResponseEntity<Void> clearCart(
            @PathVariable Long id) {

        shoppingCartService.clearCart(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}