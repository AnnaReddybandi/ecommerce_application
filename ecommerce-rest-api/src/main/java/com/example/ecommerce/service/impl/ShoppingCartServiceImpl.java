package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.cart.ShoppingCartRequestDto;
import com.example.ecommerce.dto.cart.ShoppingCartResponseDto;
import com.example.ecommerce.dto.cartitem.CartItemResponseDto;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.ShoppingCart;
import com.example.ecommerce.exception.ResourceNotFoundException;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.ShoppingCartRepository;
import com.example.ecommerce.service.ShoppingCartService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private final ShoppingCartRepository shoppingCartRepository;
    private final CustomerRepository customerRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    public ShoppingCartResponseDto create(ShoppingCartRequestDto request) {

        log.info("Creating shopping cart for customer ID: {}",
                request.customerId());

        Customer customer = customerRepository
                .findById(request.customerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found with ID: "
                                        + request.customerId()));

        ShoppingCart cart = new ShoppingCart();
        cart.setCustomer(customer);

        ShoppingCart savedCart =
                shoppingCartRepository.save(cart);

        log.info("Shopping cart created successfully with ID: {}",
                savedCart.getId());

        return mapToResponseDto(savedCart);
    }

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartResponseDto getById(Long id) {

        ShoppingCart cart = shoppingCartRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Shopping cart not found with ID: " + id));

        return mapToResponseDto(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShoppingCartResponseDto> getAll() {

        return shoppingCartRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    @Override
    public void delete(Long id) {

        ShoppingCart cart = shoppingCartRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Shopping cart not found with ID: " + id));

        cartItemRepository.deleteByShoppingCartId(id);

        shoppingCartRepository.delete(cart);

        log.info("Shopping cart deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ShoppingCartResponseDto getByCustomerId(Long customerId) {

        ShoppingCart cart = shoppingCartRepository
                .findByCustomerId(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Shopping cart not found for customer: "
                                        + customerId));

        return mapToResponseDto(cart);
    }

    @Override
    public ShoppingCartResponseDto clearCart(Long cartId) {

        ShoppingCart cart = shoppingCartRepository
                .findById(cartId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Shopping cart not found with ID: " + cartId));

        cartItemRepository.deleteByShoppingCartId(cartId);

        if (cart.getCartItems() != null) {
            cart.getCartItems().clear();
        }

        log.info("Shopping cart {} cleared successfully", cartId);

        return mapToResponseDto(cart);
    }

    private ShoppingCartResponseDto mapToResponseDto(
            ShoppingCart cart) {

        List<CartItemResponseDto> items;

        if (cart.getCartItems() == null) {
            items = List.of();
        } else {
            items = cart.getCartItems()
                    .stream()
                    .map(this::mapCartItemToResponse)
                    .toList();
        }

        return new ShoppingCartResponseDto(
                cart.getId(),
                cart.getCustomer().getId(),
                items,
                cart.getCreatedAt(),
                cart.getUpdatedAt()
        );
    }

    private CartItemResponseDto mapCartItemToResponse(
            CartItem item) {

        return new CartItemResponseDto(
                item.getId(),
                item.getShoppingCart().getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                item.getProduct().getPrice(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}