package com.example.ecommerce.scheduler;

import com.example.ecommerce.entity.ShoppingCart;
import com.example.ecommerce.repository.CartItemRepository;
import com.example.ecommerce.repository.ShoppingCartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CartCleanupScheduler {

    private final ShoppingCartRepository shoppingCartRepository;
    private final CartItemRepository cartItemRepository;

    /**
     * Removes items from carts that have been inactive
     * for more than 30 days.
     */
    @Scheduled(fixedRateString = "${app.scheduler.cart-cleanup-rate}")
    @Transactional
    public void cleanupAbandonedCarts() {

        LocalDateTime cutoffTime =
                LocalDateTime.now().minusDays(30);

        List<ShoppingCart> abandonedCarts =
                shoppingCartRepository.findAbandonedCarts(cutoffTime);

        if (abandonedCarts.isEmpty()) {
            log.debug("No abandoned carts found.");
            return;
        }

        for (ShoppingCart cart : abandonedCarts) {

            cartItemRepository.deleteByShoppingCartId(cart.getId());

            log.info(
                    "Abandoned cart {} cleaned successfully.",
                    cart.getId()
            );
        }

        log.info(
                "{} abandoned cart(s) processed.",
                abandonedCarts.size()
        );
    }
}