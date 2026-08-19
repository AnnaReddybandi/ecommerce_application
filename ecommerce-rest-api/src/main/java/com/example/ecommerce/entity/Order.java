package com.example.ecommerce.entity;

import com.example.ecommerce.entity.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_customer_id", columnList = "customer_id"),
                @Index(name = "idx_order_status", columnList = "status")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity {

    @NotNull(message = "Customer is required")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private LocalDateTime orderDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @NotBlank(message = "Shipping address is required")
    @Column(nullable = false, length = 255)
    private String shippingAddress;

    @NotNull(message = "Total amount is required")
    @DecimalMin(
            value = "0.01",
            message = "Total amount must be greater than zero"
    )
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Payment payment;

    public Order(Customer customer, String shippingAddress, BigDecimal totalAmount) {
        this.customer = customer;
        this.shippingAddress = shippingAddress;
        this.totalAmount = totalAmount;
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
    }

    public Order(Customer customer, String shippingAddress, BigDecimal totalAmount, OrderStatus status) {
        this.customer = customer;
        this.shippingAddress = shippingAddress;
        this.totalAmount = totalAmount;
        this.orderDate = LocalDateTime.now();
        this.status = status != null ? status : OrderStatus.PENDING;
    }
}