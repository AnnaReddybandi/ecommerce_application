package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.order.CheckoutRequestDto;
import com.example.ecommerce.dto.order.OrderRequestDto;
import com.example.ecommerce.dto.order.OrderResponseDto;
import com.example.ecommerce.dto.orderitem.OrderItemResponseDto;
import com.example.ecommerce.dto.payment.PaymentResponseDto;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import com.example.ecommerce.entity.enums.OrderStatus;
import com.example.ecommerce.entity.Payment;
import com.example.ecommerce.entity.enums.PaymentMethod;
import com.example.ecommerce.entity.enums.PaymentStatus;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.ShoppingCart;
import com.example.ecommerce.entity.enums.ProductStatus;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.InvalidOrderException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.PaymentRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ShoppingCartRepository;
import com.example.ecommerce.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * OrderServiceImpl
 *
 * Handles:
 * - Create order
 * - Get order by ID
 * - Get all orders
 * - Update order
 * - Delete order
 * - Confirm order
 * - Cancel order
 * - Checkout cart
 * - Get orders by status
 * - Get orders by customer
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;



    private final CustomerRepository customerRepository;

    private final ProductRepository productRepository;

    private final ShoppingCartRepository shoppingCartRepository;

    private final CartItemRepository cartItemRepository;

    private final PaymentRepository paymentRepository;


    // ============================================================
    // CREATE ORDER
    // ============================================================

    @Override
    public OrderResponseDto create(OrderRequestDto request) {

        log.info(
                "Creating order for customer ID: {}",
                request.customerId()
        );

        Customer customer =
                customerRepository.findById(request.customerId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found with ID: "
                                                + request.customerId()
                                )
                        );

        Order order = new Order();

        order.setCustomer(customer);

        order.setShippingAddress(
                request.shippingAddress()
        );

        order.setStatus(OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        List<OrderItem> orderItems =
                new ArrayList<>();


        // Create order items
        for (var itemRequest : request.items()) {

            Product product =
                    productRepository.findById(
                                    itemRequest.productId()
                            )
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Product not found with ID: "
                                                    + itemRequest.productId()
                                    )
                            );

            validateStock(
                    product,
                    itemRequest.quantity()
            );

            BigDecimal itemTotal =
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            itemRequest.quantity()
                                    )
                            );

            totalAmount =
                    totalAmount.add(itemTotal);


            OrderItem orderItem =
                    new OrderItem();

            orderItem.setOrder(order);

            orderItem.setProduct(product);

            orderItem.setQuantity(
                    itemRequest.quantity()
            );

            orderItem.setPrice(
                    product.getPrice()
            );

            orderItems.add(orderItem);


            // Reduce stock
            product.setStock(
                    product.getStock()
                            - itemRequest.quantity()
            );

            productRepository.save(product);
        }


        order.setTotalAmount(totalAmount);

        order.setOrderItems(orderItems);


        Order savedOrder =
                orderRepository.save(order);


        log.info(
                "Order created successfully. Order ID: {}",
                savedOrder.getId()
        );

        return mapToResponse(savedOrder);
    }


    // ============================================================
    // GET BY ID
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getById(Long id) {

        Order order =
                orderRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found with ID: "
                                                + id
                                )
                        );

        return mapToResponse(order);
    }


    // ============================================================
    // GET ALL
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAll() {

        return orderRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // ============================================================
    // UPDATE ORDER
    // ============================================================

    @Override
    public OrderResponseDto update(
            Long id,
            OrderRequestDto request) {

        Order order =
                orderRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found with ID: "
                                                + id
                                )
                        );


        Customer customer =
                customerRepository.findById(
                                request.customerId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found with ID: "
                                                + request.customerId()
                                )
                        );


        order.setCustomer(customer);

        order.setShippingAddress(
                request.shippingAddress()
        );


        Order updatedOrder =
                orderRepository.save(order);


        return mapToResponse(updatedOrder);
    }


    // ============================================================
    // DELETE ORDER
    // ============================================================

    @Override
    public void delete(Long id) {

        Order order =
                orderRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found with ID: "
                                                + id
                                )
                        );

        orderRepository.delete(order);

        log.info(
                "Order deleted successfully. Order ID: {}",
                id
        );
    }


    // ============================================================
    // CONFIRM ORDER
    // ============================================================

    @Override
    public OrderResponseDto confirmOrder(Long id) {

        Order order =
                orderRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found with ID: "
                                                + id
                                )
                        );


        if (order.getStatus() != OrderStatus.PENDING) {

            throw new InvalidOrderException(
                    "Only pending orders can be confirmed"
            );
        }


        order.setStatus(
                OrderStatus.CONFIRMED
        );


        Order updatedOrder =
                orderRepository.save(order);


        return mapToResponse(updatedOrder);
    }


    // ============================================================
    // CANCEL ORDER
    // ============================================================

    @Override
    public OrderResponseDto cancelOrder(Long id) {

        Order order =
                orderRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Order not found with ID: "
                                                + id
                                )
                        );


        if (order.getStatus() == OrderStatus.DELIVERED) {

            throw new InvalidOrderException(
                    "Delivered order cannot be cancelled"
            );
        }


        if (order.getStatus() == OrderStatus.CANCELLED) {

            throw new InvalidOrderException(
                    "Order is already cancelled"
            );
        }


        order.setStatus(
                OrderStatus.CANCELLED
        );

        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                if (product != null) {
                    product.setStock(product.getStock() + item.getQuantity());
                    productRepository.save(product);
                }
            }
        }

        Order updatedOrder =
                orderRepository.save(order);


        return mapToResponse(updatedOrder);
    }


    // ============================================================
    // CHECKOUT
    // ============================================================

    @Override
    public OrderResponseDto checkout(
            CheckoutRequestDto request) {

        log.info(
                "Processing checkout for customer ID: {}",
                request.customerId()
        );


        // --------------------------------------------------------
        // 1. Find customer
        // --------------------------------------------------------

        Customer customer =
                customerRepository.findById(
                                request.customerId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer not found with ID: "
                                                + request.customerId()
                                )
                        );


        // --------------------------------------------------------
        // 2. Find shopping cart
        // --------------------------------------------------------

        ShoppingCart cart =
                shoppingCartRepository
                        .findByCustomerId(
                                request.customerId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Shopping cart not found for customer: "
                                                + request.customerId()
                                )
                        );


        // --------------------------------------------------------
        // 3. Get cart items
        // --------------------------------------------------------

        List<CartItem> cartItems =
                cartItemRepository.findByShoppingCartId(
                        cart.getId()
                );


        if (cartItems.isEmpty()) {

            throw new IllegalStateException(
                    "Shopping cart is empty"
            );
        }


        // --------------------------------------------------------
        // 4. Create order
        // --------------------------------------------------------

        Order order = new Order();

        order.setCustomer(customer);

        order.setShippingAddress(
                request.shippingAddress()
        );

        order.setStatus(
                OrderStatus.PENDING
        );


        BigDecimal totalAmount =
                BigDecimal.ZERO;


        List<OrderItem> orderItems =
                new ArrayList<>();


        // --------------------------------------------------------
        // 5. Convert cart items into order items
        // --------------------------------------------------------

        for (CartItem cartItem : cartItems) {

            Product product =
                    cartItem.getProduct();


            validateStock(
                    product,
                    cartItem.getQuantity()
            );


            BigDecimal itemTotal =
                    product.getPrice()
                            .multiply(
                                    BigDecimal.valueOf(
                                            cartItem.getQuantity()
                                    )
                            );


            totalAmount =
                    totalAmount.add(itemTotal);


            OrderItem orderItem =
                    new OrderItem();

            orderItem.setOrder(order);

            orderItem.setProduct(product);

            orderItem.setQuantity(
                    cartItem.getQuantity()
            );

            orderItem.setPrice(
                    product.getPrice()
            );


            orderItems.add(orderItem);


            // Reduce product stock
            product.setStock(
                    product.getStock()
                            - cartItem.getQuantity()
            );

            productRepository.save(product);
        }


        // --------------------------------------------------------
        // 6. Set order details
        // --------------------------------------------------------

        order.setTotalAmount(
                totalAmount
        );

        order.setOrderItems(
                orderItems
        );


        // --------------------------------------------------------
        // 7. Save order
        // --------------------------------------------------------

        Order savedOrder =
                orderRepository.save(order);


        // --------------------------------------------------------
        // 8. Create payment
        // --------------------------------------------------------

        Payment payment =
                new Payment();

        payment.setOrder(
                savedOrder
        );

        payment.setAmount(
                totalAmount
        );

        payment.setMethod(
                request.paymentMethod()
        );

        payment.setStatus(
                PaymentStatus.PENDING
        );

        payment.setNotes(
                request.notes()
        );


        // Generate transaction ID
        // for online payment methods
        if (request.paymentMethod()
                != PaymentMethod.CASH_ON_DELIVERY) {

            payment.setTransactionId(
                    "TXN-" + System.currentTimeMillis()
            );
        }


        Payment savedPayment =
                paymentRepository.save(payment);


        // Connect payment with order
        savedOrder.setPayment(
                savedPayment
        );


        orderRepository.save(
                savedOrder
        );


        // --------------------------------------------------------
        // 9. Clear shopping cart
        // --------------------------------------------------------

        cartItemRepository.deleteByShoppingCartId(
                cart.getId()
        );


        cart.getCartItems().clear();


        log.info(
                "Checkout completed successfully. " +
                        "Order ID: {}, Payment ID: {}",
                savedOrder.getId(),
                savedPayment.getId()
        );


        return mapToResponse(
                savedOrder
        );
    }


    // ============================================================
    // GET ORDERS BY STATUS
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getByStatus(
            OrderStatus status) {

        return orderRepository
                .findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // ============================================================
    // GET ORDERS BY CUSTOMER
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getByCustomerId(
            Long customerId) {

        customerRepository.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with ID: "
                                        + customerId
                        )
                );


        return orderRepository
                .findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    // ============================================================
    // VALIDATE STOCK
    // ============================================================

    private void validateStock(
            Product product,
            Integer quantity) {

        if (quantity == null || quantity < 1) {

            throw new IllegalArgumentException(
                    "Quantity must be at least 1"
            );
        }


        if (product.getStatus()
                != ProductStatus.ACTIVE) {

            throw new IllegalStateException(
                    "Product is not active: "
                            + product.getName()
            );
        }


        if (product.getStock() < quantity) {

            throw new InsufficientStockException(
                    "Insufficient stock for product: "
                            + product.getName()
            );
        }
    }


    // ============================================================
    // MAP ORDER ENTITY TO RESPONSE DTO
    // ============================================================

    private OrderResponseDto mapToResponse(
            Order order) {


        List<OrderItemResponseDto> itemResponses =
                order.getOrderItems()
                        .stream()
                        .map(this::mapOrderItemToResponse)
                        .toList();


        PaymentResponseDto paymentResponse =
                null;


        if (order.getPayment() != null) {

            Payment payment =
                    order.getPayment();


            paymentResponse =
                    new PaymentResponseDto(

                            payment.getId(),

                            payment.getOrder()
                                    .getId(),

                            payment.getPaymentDate(),

                            payment.getAmount(),

                            payment.getMethod(),

                            payment.getStatus(),

                            payment.getTransactionId(),

                            payment.getNotes(),

                            payment.getCreatedAt(),

                            payment.getUpdatedAt()
                    );
        }


        return new OrderResponseDto(

                order.getId(),

                order.getCustomer()
                        .getId(),

                order.getCustomer()
                        .getName(),

                order.getOrderDate(),

                order.getStatus(),

                order.getShippingAddress(),

                order.getTotalAmount(),

                itemResponses,

                paymentResponse,

                order.getCreatedAt(),

                order.getUpdatedAt()
        );
    }


    // ============================================================
    // MAP ORDER ITEM TO RESPONSE DTO
    // ============================================================

    private OrderItemResponseDto mapOrderItemToResponse(
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