package com.example.ecommerce.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shopping_carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingCart extends BaseEntity {

    // ============================================================
    // CUSTOMER
    // ============================================================

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @jakarta.persistence.JoinColumn(
            name = "customer_id",
            nullable = false,
            unique = true
    )
    private Customer customer;


    // ============================================================
    // CART ITEMS
    // ============================================================

    @OneToMany(
            mappedBy = "shoppingCart",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<CartItem> cartItems = new ArrayList<>();


    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public ShoppingCart(Customer customer) {

        this.customer = customer;

        this.cartItems = new ArrayList<>();
    }


    // ============================================================
    // ADD ITEM
    // ============================================================

    public void addCartItem(CartItem cartItem) {

        cartItems.add(cartItem);

        cartItem.setShoppingCart(this);
    }


    // ============================================================
    // REMOVE ITEM
    // ============================================================

    public void removeCartItem(CartItem cartItem) {

        cartItems.remove(cartItem);

        cartItem.setShoppingCart(null);
    }


    // ============================================================
    // CLEAR CART
    // ============================================================

    public void clearItems() {

        for (CartItem item : cartItems) {

            item.setShoppingCart(null);
        }

        cartItems.clear();
    }
}