package com.example.ecommerce.repository;

import com.example.ecommerce.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository
        extends JpaRepository<CartItem, Long> {

    List<CartItem> findByShoppingCartId(Long cartId);

    Optional<CartItem> findByShoppingCartIdAndProductId(
            Long cartId,
            Long productId
    );

    void deleteByShoppingCartId(Long id);
}