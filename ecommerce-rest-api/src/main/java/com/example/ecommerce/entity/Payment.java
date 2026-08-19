package com.example.ecommerce.entity;

import com.example.ecommerce.entity.enums.PaymentMethod;
import com.example.ecommerce.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(
                        name = "idx_payment_order_id",
                        columnList = "order_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {

    @NotNull(message = "Order is required")
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "order_id",
            nullable = false,
            unique = true
    )
    private Order order;

    @Column(nullable = false)
    private LocalDateTime paymentDate = LocalDateTime.now();

    @NotNull(message = "Amount is required")
    @DecimalMin(
            value = "0.01",
            message = "Amount must be greater than zero"
    )
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method = PaymentMethod.CASH_ON_DELIVERY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    private String transactionId;

    private String notes;

    public Payment(Order order, BigDecimal amount, PaymentMethod method, PaymentStatus status, String transactionId, String notes) {
        this.order = order;
        this.amount = amount;
        this.method = method != null ? method : PaymentMethod.CASH_ON_DELIVERY;
        this.status = status != null ? status : PaymentStatus.PENDING;
        this.transactionId = transactionId;
        this.notes = notes;
        this.paymentDate = LocalDateTime.now();
    }
}