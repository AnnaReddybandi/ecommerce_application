package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.enums.OrderStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("""
            SELECT o
            FROM Order o
            WHERE o.customer.id = :customerId
            ORDER BY o.orderDate DESC
            """)
    List<Order> findByCustomerId(
            @Param("customerId") Long customerId
    );

    @Query("""
            SELECT o
            FROM Order o
            WHERE o.status = :status
            ORDER BY o.orderDate DESC
            """)
    List<Order> findByStatus(
            @Param("status") OrderStatus status
    );

    @Query("""
            SELECT o
            FROM Order o
            WHERE o.status = :status
            AND o.createdAt < :cutoffDate
            """)
    List<Order> findPendingOrdersOlderThan(
            @Param("status") OrderStatus status,
            @Param("cutoffDate") LocalDateTime cutoffDate
    );

    @Query(
            value = """
                    SELECT *
                    FROM orders
                    WHERE customer_id = :customerId
                    ORDER BY order_date DESC
                    """,
            nativeQuery = true
    )
    List<Order> findOrdersByCustomerIdNative(
            @Param("customerId") Long customerId
    );
}