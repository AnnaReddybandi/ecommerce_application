package com.example.ecommerce.entity;

import com.example.ecommerce.entity.enums.ProductCategory;
import com.example.ecommerce.entity.enums.ProductStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_product_category", columnList = "category"),
                @Index(name = "idx_product_status", columnList = "status")
        }
)
@NamedQuery(
        name = "Product.findActiveByCategoryNamed",
        query = """
                SELECT p
                FROM Product p
                WHERE p.category = :category
                AND p.status = 'ACTIVE'
                """
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    @NotBlank(message = "Product name is required")
    @Column(nullable = false, length = 255)
    private String name;

    @Column(length = 1000)
    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(
            value = "0.01",
            message = "Price must be greater than zero"
    )
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    @Column(nullable = false)
    private Integer stock;

    @NotNull(message = "Category is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status = ProductStatus.ACTIVE;

    private String imageUrl;

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CartItem> cartItems = new ArrayList<>();

    public Product(String name, String description, BigDecimal price, Integer stock, ProductCategory category) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.status = ProductStatus.ACTIVE;
    }

    public Product(String name, String description, BigDecimal price, Integer stock, ProductCategory category, ProductStatus status, String imageUrl) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.status = status != null ? status : ProductStatus.ACTIVE;
        this.imageUrl = imageUrl;
    }
}