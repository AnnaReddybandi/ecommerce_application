package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.orderitem.OrderItemRequestDto;
import com.example.ecommerce.dto.orderitem.OrderItemResponseDto;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.OrderItemRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.OrderItemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderItemServiceImpl
        implements OrderItemService {

    private final OrderItemRepository orderItemRepository;

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;


    // ============================================================
    // CREATE
    // ============================================================

    @Override
    public OrderItemResponseDto create(
            OrderItemRequestDto request) {

        if (request.orderId() == null) {
            throw new IllegalArgumentException("Order ID is required");
        }

        log.info(
                "Creating order item for order ID: {}",
                request.orderId()
        );

        Order order =
                orderRepository.findById(
                        request.orderId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with ID: "
                                        + request.orderId()
                        )
                );

        Product product =
                productRepository.findById(
                        request.productId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with ID: "
                                        + request.productId()
                        )
                );

        OrderItem orderItem =
                new OrderItem();

        orderItem.setOrder(order);

        orderItem.setProduct(product);

        orderItem.setQuantity(
                request.quantity()
        );

        orderItem.setPrice(
                product.getPrice()
        );

        OrderItem saved =
                orderItemRepository.save(orderItem);

        log.info(
                "Order item created successfully with ID: {}",
                saved.getId()
        );

        return mapToResponse(saved);
    }


    // ============================================================
    // GET BY ID
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public OrderItemResponseDto getById(
            Long id) {

        log.info(
                "Fetching order item with ID: {}",
                id
        );

        OrderItem orderItem =
                orderItemRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order item not found with ID: "
                                                + id
                                )
                        );

        return mapToResponse(orderItem);
    }


    // ============================================================
    // GET ALL
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponseDto> getAll() {

        log.info("Fetching all order items");

        return orderItemRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // ============================================================
    // UPDATE
    // ============================================================

    @Override
    public OrderItemResponseDto update(
            Long id,
            OrderItemRequestDto request) {

        log.info(
                "Updating order item with ID: {}",
                id
        );

        OrderItem orderItem =
                orderItemRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order item not found with ID: "
                                                + id
                                )
                        );

        Order order =
                orderRepository.findById(
                        request.orderId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with ID: "
                                        + request.orderId()
                        )
                );

        Product product =
                productRepository.findById(
                        request.productId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with ID: "
                                        + request.productId()
                        )
                );

        orderItem.setOrder(order);

        orderItem.setProduct(product);

        orderItem.setQuantity(
                request.quantity()
        );

        orderItem.setPrice(
                product.getPrice()
        );

        OrderItem updated =
                orderItemRepository.save(orderItem);

        log.info(
                "Order item updated successfully with ID: {}",
                id
        );

        return mapToResponse(updated);
    }


    // ============================================================
    // DELETE
    // ============================================================

    @Override
    public void delete(Long id) {

        log.info(
                "Deleting order item with ID: {}",
                id
        );

        OrderItem orderItem =
                orderItemRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order item not found with ID: "
                                                + id
                                )
                        );

        orderItemRepository.delete(orderItem);

        log.info(
                "Order item deleted successfully with ID: {}",
                id
        );
    }


    // ============================================================
    // GET BY ORDER ID
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<OrderItemResponseDto> getByOrderId(
            Long orderId) {

        log.info(
                "Fetching order items for order ID: {}",
                orderId
        );

        orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Order not found with ID: "
                                        + orderId
                        )
                );

        return orderItemRepository
                .findByOrderId(orderId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // ============================================================
    // ENTITY -> RESPONSE DTO
    // ============================================================

    private OrderItemResponseDto mapToResponse(
            OrderItem item) {

        BigDecimal subtotal =
                item.getPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        item.getQuantity()
                                )
                        );

        return new OrderItemResponseDto(

                item.getId(),

                item.getOrder().getId(),

                item.getProduct().getId(),

                item.getProduct().getName(),

                item.getQuantity(),

                item.getPrice(),

                subtotal,

                item.getCreatedAt(),

                item.getUpdatedAt()
        );
    }
}