package com.example.ecommerce.scheduler;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.enums.OrderStatus;
import com.example.ecommerce.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderCancellationSchedulerTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderCancellationScheduler scheduler;

    @Test
    @DisplayName("Cancel expired pending orders")
    void cancelExpiredOrders_Success() {
        Order order = new Order();
        order.setId(50L);
        order.setStatus(OrderStatus.PENDING);

        when(orderRepository.findPendingOrdersOlderThan(eq(OrderStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(List.of(order));

        scheduler.cancelExpiredOrders();

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepository).saveAll(List.of(order));
    }

    @Test
    @DisplayName("No expired orders does not save")
    void cancelExpiredOrders_Empty() {
        when(orderRepository.findPendingOrdersOlderThan(eq(OrderStatus.PENDING), any(LocalDateTime.class)))
                .thenReturn(List.of());

        scheduler.cancelExpiredOrders();

        verify(orderRepository, never()).saveAll(any());
    }
}
