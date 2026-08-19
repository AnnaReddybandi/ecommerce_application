package com.example.ecommerce.controller.v2;

import com.example.ecommerce.dto.product.ProductRequestDto;
import com.example.ecommerce.dto.product.ProductResponseDto;
import com.example.ecommerce.entity.enums.ProductCategory;
import com.example.ecommerce.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v2/products")
@Tag(
        name = "Products V2",
        description = "Product Management APIs (Version 2)"
)
public class ProductV2Controller {

    private final ProductService productService;

    public ProductV2Controller(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @Operation(summary = "Create a new product (v2)")
    public ResponseEntity<ProductResponseDto> create(
            @Valid @RequestBody ProductRequestDto request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.create(request));
    }

    @GetMapping
    @Operation(summary = "Get all products (v2)")
    public ResponseEntity<List<ProductResponseDto>> getAll() {

        return ResponseEntity.ok(
                productService.getAll()
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID (v2)")
    public ResponseEntity<ProductResponseDto> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                productService.getById(id)
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product details (v2)")
    public ResponseEntity<ProductResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto request) {

        return ResponseEntity.ok(
                productService.update(id, request)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product by ID (v2)")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        productService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/reduce-stock")
    @Operation(summary = "Reduce product stock (v2)")
    public ResponseEntity<ProductResponseDto> reduceStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(
                productService.reduceStock(id, quantity)
        );
    }

    @PostMapping("/{id}/increase-stock")
    @Operation(summary = "Increase product stock (v2)")
    public ResponseEntity<ProductResponseDto> increaseStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(
                productService.increaseStock(id, quantity)
        );
    }

    @PostMapping(
            value = "/{id}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @Operation(summary = "Upload product image (v2)")
    public ResponseEntity<ProductResponseDto> uploadImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {

        return ResponseEntity.ok(
                productService.uploadImage(id, file)
        );
    }

    @GetMapping("/price-range")
    @Operation(summary = "Find products by price range (v2)")
    public ResponseEntity<List<ProductResponseDto>> getByPriceRange(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {

        return ResponseEntity.ok(
                productService.getByPriceRange(min, max)
        );
    }

    @GetMapping("/low-stock/{threshold}")
    @Operation(summary = "Find products with low stock (v2)")
    public ResponseEntity<List<ProductResponseDto>> getLowStockProducts(
            @PathVariable Integer threshold) {

        return ResponseEntity.ok(
                productService.getLowStockProducts(threshold)
        );
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Find products by category (v2)")
    public ResponseEntity<List<ProductResponseDto>> getByCategory(
            @PathVariable ProductCategory category) {

        return ResponseEntity.ok(
                productService.getByCategory(category)
        );
    }
}