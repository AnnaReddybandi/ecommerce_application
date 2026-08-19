package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Payment;
import com.example.ecommerce.entity.enums.PaymentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    @Query("""
            SELECT p
            FROM Payment p
            WHERE p.order.id = :orderId
            """)
    Optional<Payment> findByOrderId(
            @Param("orderId") Long orderId
    );

    @Query("""
            SELECT p
            FROM Payment p
            WHERE p.status = :status
            ORDER BY p.paymentDate DESC
            """)
    List<Payment> findByStatus(
            @Param("status") PaymentStatus status
    );

    @Query("""
            SELECT p
            FROM Payment p
            WHERE p.paymentDate BETWEEN :startDate AND :endDate
            AND p.status = :status
            """)
    List<Payment> findPaymentsByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("status") PaymentStatus status
    );

    @Query(
            value = """
                    SELECT *
                    FROM payments
                    WHERE transaction_id = :transactionId
                    """,
            nativeQuery = true
    )
    Optional<Payment> findByTransactionId(
            @Param("transactionId") String transactionId
    );
}