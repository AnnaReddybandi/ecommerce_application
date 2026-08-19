package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.cartitem.CartItemRequestDto;
import com.example.ecommerce.dto.cartitem.CartItemResponseDto;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.ShoppingCart;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ShoppingCartRepository;
import com.example.ecommerce.service.CartItemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductRepository productRepository;

    // ============================================================
    // CREATE / ADD ITEM TO CART
    // ============================================================

    @Override
    public CartItemResponseDto create(CartItemRequestDto request) {

        log.info(
                "Adding product ID {} to cart ID {}",
                request.productId(),
                request.cartId()
        );

        // Find shopping cart
        ShoppingCart cart = shoppingCartRepository
                .findById(request.cartId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Shopping cart not found with ID: "
                                        + request.cartId()
                        )
                );

        // Find product
        Product product = productRepository
                .findById(request.productId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found with ID: "
                                        + request.productId()
                        )
                );

        // Validate requested quantity
        validateStock(product, request.quantity());

        // Check whether product already exists in cart
        CartItem existingItem =
                cartItemRepository
                        .findByShoppingCartIdAndProductId(
                                request.cartId(),
                                request.productId()
                        )
                        .orElse(null);

        // ========================================================
        // PRODUCT ALREADY EXISTS IN CART
        // ========================================================

        if (existingItem != null) {

            int newQuantity =
                    existingItem.getQuantity()
                            + request.quantity();

            validateStock(product, newQuantity);

            existingItem.setQuantity(newQuantity);

            CartItem savedItem =
                    cartItemRepository.save(existingItem);

            log.info(
                    "Existing cart item updated successfully. " +
                            "Cart ID: {}, Product ID: {}, Quantity: {}",
                    request.cartId(),
                    request.productId(),
                    newQuantity
            );

            return mapToResponse(savedItem);
        }

        // ========================================================
        // NEW CART ITEM
        // ========================================================

        CartItem cartItem = new CartItem();

        cartItem.setShoppingCart(cart);
        cartItem.setProduct(product);
        cartItem.setQuantity(request.quantity());

        CartItem savedItem =
                cartItemRepository.save(cartItem);

        log.info(
                "Cart item created successfully with ID: {}",
                savedItem.getId()
        );

        return mapToResponse(savedItem);
    }

    // ============================================================
    // GET ALL CART ITEMS
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<CartItemResponseDto> getAll() {

        log.info("Fetching all cart items");

        return cartItemRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ============================================================
    // GET CART ITEM BY ID
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public CartItemResponseDto getById(Long id) {

        log.info(
                "Fetching cart item with ID: {}",
                id
        );

        CartItem cartItem =
                cartItemRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cart item not found with ID: "
                                                + id
                                )
                        );

        return mapToResponse(cartItem);
    }

    // ============================================================
    // GET CART ITEMS BY CART ID
    // ============================================================

    @Override
    @Transactional(readOnly = true)
    public List<CartItemResponseDto> getByCartId(Long cartId) {

        log.info(
                "Fetching cart items for cart ID: {}",
                cartId
        );

        // Verify shopping cart exists
        if (!shoppingCartRepository.existsById(cartId)) {

            throw new ResourceNotFoundException(
                    "Shopping cart not found with ID: "
                            + cartId
            );
        }

        return cartItemRepository
                .findByShoppingCartId(cartId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ============================================================
    // UPDATE CART ITEM QUANTITY
    // ============================================================

    @Override
    public CartItemResponseDto update(
            Long id,
            CartItemRequestDto request) {

        log.info(
                "Updating cart item ID: {}",
                id
        );

        CartItem cartItem =
                cartItemRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cart item not found with ID: "
                                                + id
                                )
                        );

        // ========================================================
        // PREVENT CART CHANGE
        // ========================================================

        if (!cartItem.getShoppingCart()
                .getId()
                .equals(request.cartId())) {

            throw new IllegalArgumentException(
                    "Cart ID cannot be changed while updating a cart item"
            );
        }

        // ========================================================
        // PREVENT PRODUCT CHANGE
        // ========================================================

        if (!cartItem.getProduct()
                .getId()
                .equals(request.productId())) {

            throw new IllegalArgumentException(
                    "Product ID cannot be changed while updating a cart item"
            );
        }

        Product product = cartItem.getProduct();

        // ========================================================
        // VALIDATE STOCK
        // ========================================================

        validateStock(
                product,
                request.quantity()
        );

        // ========================================================
        // UPDATE QUANTITY
        // ========================================================

        cartItem.setQuantity(
                request.quantity()
        );

        CartItem updatedItem =
                cartItemRepository.save(cartItem);

        log.info(
                "Cart item updated successfully with ID: {}",
                id
        );

        return mapToResponse(updatedItem);
    }

    // ============================================================
    // DELETE CART ITEM
    // ============================================================

    @Override
    public void delete(Long id) {

        log.info(
                "Deleting cart item with ID: {}",
                id
        );

        CartItem cartItem =
                cartItemRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cart item not found with ID: "
                                                + id
                                )
                        );

        cartItemRepository.delete(cartItem);

        log.info(
                "Cart item deleted successfully with ID: {}",
                id
        );
    }

    // ============================================================
    // STOCK VALIDATION
    // ============================================================

    private void validateStock(
            Product product,
            Integer quantity) {

        if (quantity == null || quantity < 1) {

            throw new IllegalArgumentException(
                    "Quantity must be at least 1"
            );
        }

        if (product.getStock() < quantity) {

            throw new InsufficientStockException(
                    "Insufficient stock for product: "
                            + product.getName()
                            + ". Available stock: "
                            + product.getStock()
            );
        }
    }

    // ============================================================
    // ENTITY -> RESPONSE DTO
    // ============================================================

    private CartItemResponseDto mapToResponse(
            CartItem cartItem) {

        return new CartItemResponseDto(
                cartItem.getId(),
                cartItem.getShoppingCart().getId(),
                cartItem.getProduct().getId(),
                cartItem.getProduct().getName(),
                cartItem.getQuantity(),
                cartItem.getProduct().getPrice(),
                cartItem.getCreatedAt(),
                cartItem.getUpdatedAt()
        );
    }
}