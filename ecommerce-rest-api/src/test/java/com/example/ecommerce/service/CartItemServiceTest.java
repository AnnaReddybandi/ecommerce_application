package com.example.ecommerce.service;

import com.example.ecommerce.dto.cartitem.CartItemRequestDto;
import com.example.ecommerce.dto.cartitem.CartItemResponseDto;
import com.example.ecommerce.entity.CartItem;
import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.Product;
import com.example.ecommerce.entity.enums.ProductCategory;
import com.example.ecommerce.entity.ShoppingCart;
import com.example.ecommerce.exception.InsufficientStockException;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.repository.ShoppingCartRepository;
import com.example.ecommerce.service.impl.CartItemServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ShoppingCartRepository shoppingCartRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartItemServiceImpl cartItemService;

    private ShoppingCart cart;
    private Product product;
    private CartItem cartItem;

    @BeforeEach
    void setUp() {
        Customer customer = new Customer();
        customer.setId(1L);

        cart = new ShoppingCart();
        cart.setId(10L);
        cart.setCustomer(customer);

        product = new Product();
        product.setId(20L);
        product.setName("Headphones");
        product.setPrice(new BigDecimal("150.00"));
        product.setStock(10);
        product.setStatus(ProductStatus.ACTIVE);
        product.setCategory(ProductCategory.ELECTRONICS);

        cartItem = new CartItem(cart, product, 2);
        cartItem.setId(100L);
        cartItem.setCreatedAt(LocalDateTime.now());
        cartItem.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Add new item to cart successfully")
    void createCartItem_Success() {
        CartItemRequestDto request = new CartItemRequestDto(10L, 20L, 2);

        when(shoppingCartRepository.findById(10L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(20L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByShoppingCartIdAndProductId(10L, 20L)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        CartItemResponseDto response = cartItemService.create(request);

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.quantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("Add existing item to cart increases quantity")
    void createCartItem_ExistingItem_IncreasesQuantity() {
        CartItemRequestDto request = new CartItemRequestDto(10L, 20L, 3);

        when(shoppingCartRepository.findById(10L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(20L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByShoppingCartIdAndProductId(10L, 20L)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);

        CartItemResponseDto response = cartItemService.create(request);

        assertThat(response).isNotNull();
        assertThat(cartItem.getQuantity()).isEqualTo(5);
    }

    @Test
    @DisplayName("Add item with stock exceeding throws InsufficientStockException")
    void createCartItem_InsufficientStock_ThrowsException() {
        CartItemRequestDto request = new CartItemRequestDto(10L, 20L, 50);

        when(shoppingCartRepository.findById(10L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(20L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByShoppingCartIdAndProductId(10L, 20L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartItemService.create(request))
                .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    @DisplayName("Get cart items by cart ID")
    void getByCartId_Success() {
        when(shoppingCartRepository.existsById(10L)).thenReturn(true);
        when(cartItemRepository.findByShoppingCartId(10L)).thenReturn(List.of(cartItem));

        List<CartItemResponseDto> list = cartItemService.getByCartId(10L);

        assertThat(list).hasSize(1);
        assertThat(list.get(0).productName()).isEqualTo("Headphones");
    }

    @Test
    @DisplayName("Delete cart item successfully")
    void deleteCartItem_Success() {
        when(cartItemRepository.findById(100L)).thenReturn(Optional.of(cartItem));

        cartItemService.delete(100L);

        verify(cartItemRepository).delete(cartItem);
    }
}
