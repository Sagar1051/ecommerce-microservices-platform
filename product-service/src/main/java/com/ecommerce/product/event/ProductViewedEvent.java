package com.ecommerce.product.event;

import lombok.Builder;

import java.time.Instant;

/**
 * Published to Kafka topic "product-events" whenever a product is viewed.
 * The recommendation-service (built later) will consume this topic to build
 * a user's interaction history for collaborative filtering.
 */
@Builder
public record ProductViewedEvent(
        Long productId,
        String productName,
        String category,
        Long userId,
        Instant viewedAt
) {}
