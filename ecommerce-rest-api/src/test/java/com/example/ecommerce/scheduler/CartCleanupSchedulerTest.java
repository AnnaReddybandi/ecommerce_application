package com.example.ecommerce.scheduler;

import com.example.ecommerce.entity.Customer;
import com.example.ecommerce.entity.ShoppingCart;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.ShoppingCartRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartCleanupSchedulerTest {

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartCleanupScheduler scheduler;

    @Test
    @DisplayName("Clean abandoned carts deletes items")
    void cleanupAbandonedCarts_Success() {
        ShoppingCart cart = new ShoppingCart();
        cart.setId(100L);

        when(shoppingCartRepository.findAbandonedCarts(any(LocalDateTime.class))).thenReturn(List.of(cart));

        scheduler.cleanupAbandonedCarts();

        verify(cartItemRepository).deleteByShoppingCartId(100L);
    }

    @Test
    @DisplayName("No abandoned carts does not call delete")
    void cleanupAbandonedCarts_Empty() {
        when(shoppingCartRepository.findAbandonedCarts(any(LocalDateTime.class))).thenReturn(List.of());

        scheduler.cleanupAbandonedCarts();

        verify(cartItemRepository, never()).deleteByShoppingCartId(anyLong());
    }
}
