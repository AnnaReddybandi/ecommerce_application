package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.product.ProductRequestDto;
import com.example.ecommerce.dto.product.ProductResponseDto;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.enums.ProductCategory;
import com.example.ecommerce.entity.enums.ProductStatus;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.ProductService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Value("${file.upload.dir}")
    private String uploadDirectory;

    // ============================================================
    // CREATE PRODUCT
    // ============================================================

    @Override
    public ProductResponseDto create(
            ProductRequestDto request) {

        log.info(
                "Creating product: {}",
                request.name()
        );

        Product product = new Product();

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategory(request.category());

        if (request.status() != null) {
            product.setStatus(request.status());
        } else {
            product.setStatus(ProductStatus.ACTIVE);
        }

        if (request.imageUrl() != null
                && !request.imageUrl().isBlank()) {

            product.setImageUrl(request.imageUrl());
        }

        Product savedProduct =
                productRepository.save(product);

        log.info(
                "Product created successfully. ID: {}",
                savedProduct.getId()
        );

        return mapToResponse(savedProduct);
    }

    // ============================================================
    // GET ALL PRODUCTS
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getAll() {

        log.info("Fetching all products");

        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ============================================================
    // GET PRODUCT BY ID
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getById(Long id) {

        log.info(
                "Fetching product with ID: {}",
                id
        );

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found with ID: "
                                                + id
                                )
                        );

        return mapToResponse(product);
    }

    // ============================================================
    // UPDATE PRODUCT
    // ============================================================

    @Override
    public ProductResponseDto update(
            Long id,
            ProductRequestDto request) {

        log.info(
                "Updating product with ID: {}",
                id
        );

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found with ID: "
                                                + id
                                )
                        );

        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setCategory(request.category());

        if (request.status() != null) {
            product.setStatus(request.status());
        }

        if (request.imageUrl() != null
                && !request.imageUrl().isBlank()) {

            product.setImageUrl(request.imageUrl());
        }

        Product updatedProduct =
                productRepository.save(product);

        log.info(
                "Product updated successfully. ID: {}",
                id
        );

        return mapToResponse(updatedProduct);
    }

    // ============================================================
    // DELETE PRODUCT
    // ============================================================

    @Override
    public void delete(Long id) {

        log.info(
                "Deleting product with ID: {}",
                id
        );

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found with ID: "
                                                + id
                                )
                        );

        productRepository.delete(product);

        log.info(
                "Product deleted successfully. ID: {}",
                id
        );
    }

    // ============================================================
    // REDUCE STOCK
    // ============================================================

    @Override
    public ProductResponseDto reduceStock(
            Long id,
            Integer quantity) {

        log.info(
                "Reducing stock. Product ID: {}, Quantity: {}",
                id,
                quantity
        );

        validateQuantity(quantity);

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found with ID: "
                                                + id
                                )
                        );

        if (product.getStock() < quantity) {

            throw new InsufficientStockException(
                    "Insufficient stock. Available stock: "
                            + product.getStock()
            );
        }

        product.setStock(
                product.getStock() - quantity
        );

        Product updatedProduct =
                productRepository.save(product);

        return mapToResponse(updatedProduct);
    }

    // ============================================================
    // INCREASE STOCK
    // ============================================================

    @Override
    public ProductResponseDto increaseStock(
            Long id,
            Integer quantity) {

        log.info(
                "Increasing stock. Product ID: {}, Quantity: {}",
                id,
                quantity
        );

        validateQuantity(quantity);

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found with ID: "
                                                + id
                                )
                        );

        product.setStock(
                product.getStock() + quantity
        );

        Product updatedProduct =
                productRepository.save(product);

        return mapToResponse(updatedProduct);
    }

    // ============================================================
    // UPLOAD PRODUCT IMAGE
    // ============================================================

    @Override
    public ProductResponseDto uploadImage(
            Long id,
            MultipartFile file) {

        log.info(
                "Uploading image for product ID: {}",
                id
        );

        if (file == null || file.isEmpty()) {

            throw new IllegalArgumentException(
                    "Image file is required"
            );
        }

        Product product =
                productRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found with ID: "
                                                + id
                                )
                        );

        try {

            Path uploadPath =
                    Paths.get(uploadDirectory)
                            .toAbsolutePath()
                            .normalize();

            Files.createDirectories(uploadPath);

            String originalFileName =
                    file.getOriginalFilename();

            String extension = "";

            if (originalFileName != null
                    && originalFileName.contains(".")) {

                extension =
                        originalFileName.substring(
                                originalFileName.lastIndexOf(".")
                        );
            }

            String fileName =
                    UUID.randomUUID()
                            + extension;

            Path targetPath =
                    uploadPath.resolve(fileName)
                            .normalize();

            if (!targetPath.startsWith(uploadPath)) {

                throw new IllegalArgumentException(
                        "Invalid file path"
                );
            }

            Files.copy(
                    file.getInputStream(),
                    targetPath,
                    StandardCopyOption.REPLACE_EXISTING
            );

            String imageUrl =
                    "/uploads/products/" + fileName;

            product.setImageUrl(imageUrl);

            Product updatedProduct =
                    productRepository.save(product);

            log.info(
                    "Product image uploaded successfully. Product ID: {}",
                    id
            );

            return mapToResponse(updatedProduct);

        } catch (IOException exception) {

            log.error(
                    "Failed to upload product image",
                    exception
            );

            throw new RuntimeException(
                    "Failed to upload product image",
                    exception
            );
        }
    }

    // ============================================================
    // PRICE RANGE
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getByPriceRange(
            BigDecimal min,
            BigDecimal max) {

        validatePriceRange(min, max);

        return productRepository
                .findByPriceRange(
                        min,
                        max,
                        ProductStatus.ACTIVE
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ============================================================
    // LOW STOCK
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getLowStockProducts(
            Integer threshold) {

        if (threshold == null || threshold < 0) {

            throw new IllegalArgumentException(
                    "Stock threshold cannot be negative"
            );
        }

        return productRepository
                .findLowStockProducts(threshold)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ============================================================
    // CATEGORY
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> getByCategory(
            ProductCategory category) {

        if (category == null) {

            throw new IllegalArgumentException(
                    "Product category is required"
            );
        }

        return productRepository
                .findByCategoryAndStatus(
                        category,
                        ProductStatus.ACTIVE
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ============================================================
    // NATIVE QUERY - MINIMUM STOCK
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> queryByMinimumStock(
            Integer minimumStock) {

        if (minimumStock == null || minimumStock < 0) {

            throw new IllegalArgumentException(
                    "Minimum stock cannot be negative"
            );
        }

        return productRepository
                .findProductsWithStock(minimumStock)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ============================================================
    // JPQL QUERY - PRICE RANGE
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> queryByPriceRange(
            BigDecimal min,
            BigDecimal max) {

        validatePriceRange(min, max);

        return productRepository
                .findByPriceRange(
                        min,
                        max,
                        ProductStatus.ACTIVE
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ============================================================
    // NAMED QUERY - CATEGORY
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponseDto> queryByCategory(
            ProductCategory category) {

        if (category == null) {

            throw new IllegalArgumentException(
                    "Product category is required"
            );
        }

        return productRepository
                .findActiveByCategoryNamed(category)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ============================================================
    // VALIDATE QUANTITY
    // ============================================================

    private void validateQuantity(Integer quantity) {

        if (quantity == null || quantity <= 0) {

            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }
    }

    // ============================================================
    // VALIDATE PRICE RANGE
    // ============================================================

    private void validatePriceRange(
            BigDecimal min,
            BigDecimal max) {

        if (min == null || max == null) {

            throw new IllegalArgumentException(
                    "Minimum and maximum price are required"
            );
        }

        if (min.compareTo(BigDecimal.ZERO) < 0
                || max.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Price cannot be negative"
            );
        }

        if (min.compareTo(max) > 0) {

            throw new IllegalArgumentException(
                    "Minimum price cannot be greater than maximum price"
            );
        }
    }

    // ============================================================
    // ENTITY -> RESPONSE DTO
    // ============================================================

    private ProductResponseDto mapToResponse(
            Product product) {

        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getCategory(),
                product.getStatus(),
                product.getImageUrl(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}