package com.ecommerce.order.event;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Published to Kafka topic "order-events" when an order is placed.
 * recommendation-service will consume this as a much stronger purchase
 * signal than a product view (someone actually bought this).
 */
@Builder
public record OrderPlacedEvent(
        Long orderId,
        Long userId,
        List<OrderedItem> items,
        BigDecimal totalAmount,
        Instant placedAt
) {
    public record OrderedItem(
            Long productId,
            Integer quantity
    ) {}
}
