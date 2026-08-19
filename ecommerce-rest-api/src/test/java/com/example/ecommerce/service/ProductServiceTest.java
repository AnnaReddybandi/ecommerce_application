package com.example.ecommerce.service;

import com.example.ecommerce.dto.product.ProductRequestDto;
import com.example.ecommerce.dto.product.ProductResponseDto;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.enums.ProductCategory;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequestDto requestDto;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Smartphone");
        product.setDescription("Flagship device");
        product.setPrice(new BigDecimal("999.99"));
        product.setStock(50);
        product.setCategory(ProductCategory.ELECTRONICS);
        product.setStatus(ProductStatus.ACTIVE);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        requestDto = new ProductRequestDto(
                "Smartphone",
                "Flagship device",
                new BigDecimal("999.99"),
                50,
                ProductCategory.ELECTRONICS,
                ProductStatus.ACTIVE,
                null
        );
    }

    @Test
    @DisplayName("Create product successfully")
    void createProduct_Success() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDto response = productService.create(requestDto);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Smartphone");
    }

    @Test
    @DisplayName("Get product by ID successfully")
    void getById_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponseDto response = productService.getById(1L);

        assertThat(response).isNotNull();
        assertThat(response.name()).isEqualTo("Smartphone");
    }

    @Test
    @DisplayName("Get product by non-existent ID throws ResourceNotFoundException")
    void getById_NotFound_ThrowsException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product not found with ID");
    }

    @Test
    @DisplayName("Reduce stock successfully")
    void reduceStock_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDto response = productService.reduceStock(1L, 10);

        assertThat(response).isNotNull();
        assertThat(product.getStock()).isEqualTo(40);
    }

    @Test
    @DisplayName("Reduce stock beyond available throws InsufficientStockException")
    void reduceStock_Insufficient_ThrowsException() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.reduceStock(1L, 100))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    @DisplayName("Increase stock successfully")
    void increaseStock_Success() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponseDto response = productService.increaseStock(1L, 20);

        assertThat(response).isNotNull();
        assertThat(product.getStock()).isEqualTo(70);
    }

    @Test
    @DisplayName("Find products by category")
    void getByCategory_Success() {
        when(productRepository.findByCategoryAndStatus(ProductCategory.ELECTRONICS, ProductStatus.ACTIVE))
                .thenReturn(List.of(product));

        List<ProductResponseDto> list = productService.getByCategory(ProductCategory.ELECTRONICS);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).category()).isEqualTo(ProductCategory.ELECTRONICS);
    }

    @Test
    @DisplayName("Find products by price range")
    void getByPriceRange_Success() {
        when(productRepository.findByPriceRange(new BigDecimal("500"), new BigDecimal("1500"), ProductStatus.ACTIVE))
                .thenReturn(List.of(product));

        List<ProductResponseDto> list = productService.getByPriceRange(new BigDecimal("500"), new BigDecimal("1500"));

        assertThat(list).hasSize(1);
    }
}
