package com.ecommerce.recommendation.event;

import java.time.Instant;

public record ProductViewedEvent(
        Long productId,
        String productName,
        String category,
        Long userId,
        Instant viewedAt
) {}
