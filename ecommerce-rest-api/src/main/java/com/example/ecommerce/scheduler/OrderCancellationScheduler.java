package com.example.ecommerce.scheduler;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.enums.OrderStatus;
import com.example.ecommerce.repository.OrderRepository;
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
public class OrderCancellationScheduler {

    private final OrderRepository orderRepository;

    /**
     * Cancels pending orders older than 30 minutes.
     *
     * Runs according to:
     * app.scheduler.order-cancellation-rate
     */
    @Scheduled(fixedRateString = "${app.scheduler.order-cancellation-rate}")
    @Transactional
    public void cancelExpiredOrders() {

        LocalDateTime cutoffTime =
                LocalDateTime.now().minusMinutes(30);

        List<Order> expiredOrders =
                orderRepository.findPendingOrdersOlderThan(
                        OrderStatus.PENDING,
                        cutoffTime
                );

        if (expiredOrders.isEmpty()) {
            log.debug("No expired pending orders found.");
            return;
        }

        for (Order order : expiredOrders) {

            order.setStatus(OrderStatus.CANCELLED);

            log.info(
                    "Order {} automatically cancelled because it was pending before {}",
                    order.getId(),
                    cutoffTime
            );
        }

        orderRepository.saveAll(expiredOrders);

        log.info(
                "{} expired order(s) cancelled successfully.",
                expiredOrders.size()
        );
    }
}