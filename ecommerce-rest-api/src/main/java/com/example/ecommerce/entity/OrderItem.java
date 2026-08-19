package com.example.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
        name = "order_items",
        indexes = {
                @Index(
                        name = "idx_order_id",
                        columnList = "order_id"
                ),
                @Index(
                        name = "idx_order_product_id",
                        columnList = "product_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class OrderItem extends BaseEntity {

    @NotNull(message = "Order is required")
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "order_id",
            nullable = false
    )
    private Order order;

    @NotNull(message = "Product is required")
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "product_id",
            nullable = false
    )
    private Product product;

    @NotNull(message = "Quantity is required")
    @Min(
            value = 1,
            message = "Quantity must be at least 1"
    )
    @Column(nullable = false)
    private Integer quantity;

    @NotNull(message = "Price is required")
    @DecimalMin(
            value = "0.01",
            message = "Price must be greater than zero"
    )
    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal price;

    public OrderItem(Order order, Product product, Integer quantity, BigDecimal price) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }
}