package com.example.ecommerce.repository;

import com.example.ecommerce.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {

    // ============================================================
    // FIND BY ORDER ID
    // ============================================================

    @Query("""
            SELECT oi
            FROM OrderItem oi
            WHERE oi.order.id = :orderId
            """)
    List<OrderItem> findByOrderId(
            @Param("orderId") Long orderId
    );

    // ============================================================
    // FIND BY PRODUCT ID
    // ============================================================

    @Query("""
            SELECT oi
            FROM OrderItem oi
            WHERE oi.product.id = :productId
            """)
    List<OrderItem> findByProductId(
            @Param("productId") Long productId
    );

    // ============================================================
    // NATIVE QUERY
    // ============================================================

    @Query(
            value = """
                    SELECT *
                    FROM order_items
                    WHERE order_id = :orderId
                    ORDER BY id
                    """,
            nativeQuery = true
    )
    List<OrderItem> findOrderItemsDetails(
            @Param("orderId") Long orderId
    );
}