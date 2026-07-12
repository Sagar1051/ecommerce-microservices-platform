package com.ecommerce.recommendation.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

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
