package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.enums.ProductCategory;
import com.example.ecommerce.entity.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository
        extends JpaRepository<Product, Long> {

    // ============================================================
    // JPQL - CATEGORY + STATUS
    // ============================================================

    @Query("""
            SELECT p
            FROM Product p
            WHERE p.category = :category
            AND p.status = :status
            """)
    List<Product> findByCategoryAndStatus(
            @Param("category") ProductCategory category,
            @Param("status") ProductStatus status
    );

    // ============================================================
    // JPQL - PRICE RANGE
    // ============================================================

    @Query("""
            SELECT p
            FROM Product p
            WHERE p.price BETWEEN :min AND :max
            AND p.status = :status
            ORDER BY p.price ASC
            """)
    List<Product> findByPriceRange(
            @Param("min") BigDecimal min,
            @Param("max") BigDecimal max,
            @Param("status") ProductStatus status
    );

    // ============================================================
    // NATIVE SQL - STOCK
    // ============================================================

    @Query(
            value = """
                    SELECT *
                    FROM products
                    WHERE stock > :minimumStock
                    ORDER BY stock DESC
                    """,
            nativeQuery = true
    )
    List<Product> findProductsWithStock(
            @Param("minimumStock") int minimumStock
    );

    // ============================================================
    // JPQL - LOW STOCK
    // ============================================================

    @Query("""
            SELECT p
            FROM Product p
            WHERE p.stock < :threshold
            AND p.status = 'ACTIVE'
            ORDER BY p.stock ASC
            """)
    List<Product> findLowStockProducts(
            @Param("threshold") Integer threshold
    );

    // ============================================================
    // NAMED QUERY
    // ============================================================

    List<Product> findActiveByCategoryNamed(
            @Param("category") ProductCategory category
    );

    // ============================================================
    // DERIVED QUERIES
    // ============================================================

    List<Product> findByCategory(
            ProductCategory category
    );

    List<Product> findByStatus(
            ProductStatus status
    );
}