package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository
        extends JpaRepository<Customer, Long> {

    @Query("""
            SELECT c
            FROM Customer c
            WHERE c.email = :email
            """)
    Optional<Customer> findByEmail(
            @Param("email") String email
    );

    @Query("""
            SELECT c
            FROM Customer c
            WHERE c.phone = :phone
            """)
    Optional<Customer> findByPhone(
            @Param("phone") String phone
    );

    @Query(
            value = """
                    SELECT *
                    FROM customers
                    WHERE email = :email
                    """,
            nativeQuery = true
    )
    Optional<Customer> findByEmailNative(
            @Param("email") String email
    );
}