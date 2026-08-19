package com.example.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "cart_items",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cart_product",
                        columnNames = {
                                "shopping_cart_id",
                                "product_id"
                        }
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
public class CartItem extends BaseEntity {

    @NotNull(message = "Shopping cart is required")
    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "shopping_cart_id",
            nullable = false
    )
    private ShoppingCart shoppingCart;


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

    public CartItem(ShoppingCart shoppingCart, Product product, Integer quantity) {
        this.shoppingCart = shoppingCart;
        this.product = product;
        this.quantity = quantity;
    }
}