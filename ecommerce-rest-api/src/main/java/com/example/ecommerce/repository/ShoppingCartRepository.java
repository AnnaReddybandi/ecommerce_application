package com.example.ecommerce.repository;

import com.example.ecommerce.entity.ShoppingCart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShoppingCartRepository
        extends JpaRepository<ShoppingCart, Long> {

    // ============================================================
    // FIND CART BY CUSTOMER ID
    // ============================================================

    Optional<ShoppingCart> findByCustomerId(Long customerId);


    // ============================================================
    // CHECK CART EXISTS FOR CUSTOMER
    // ============================================================

    boolean existsByCustomerId(Long customerId);


    // ============================================================
    // FIND ABANDONED CARTS
    //
    // A cart is considered abandoned when:
    // 1. It has at least one cart item
    // 2. Its updatedAt is before the given cutoff time
    // ============================================================

    @Query("""
            SELECT DISTINCT sc
            FROM ShoppingCart sc
            JOIN sc.cartItems ci
            WHERE sc.updatedAt < :cutoffTime
            """)
    List<ShoppingCart> findAbandonedCarts(
            @Param("cutoffTime") LocalDateTime cutoffTime
    );
}