package com.ecommerce.product.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record ProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String category,
        Integer stockQuantity,
        String imageUrl,
        Instant createdAt,
        Instant updatedAt
) {}
