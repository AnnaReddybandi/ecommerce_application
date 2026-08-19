package com.example.ecommerce.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "customers",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends BaseEntity {

    @NotBlank(message = "Customer name is required")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Phone is required")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Phone should be 10 digits"
    )
    @Column(nullable = false, length = 20)
    private String phone;

    @NotBlank(message = "Address is required")
    @Column(nullable = false, length = 255)
    private String address;

    @OneToMany(
            mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Order> orders = new ArrayList<>();

    @OneToOne(
            mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private ShoppingCart shoppingCart;

    public Customer(String name, String email, String phone, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
}