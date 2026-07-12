package com.ecommerce.recommendation.client;

import java.math.BigDecimal;

public record ProductDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        String category,
        Integer stockQuantity,
        String imageUrl
) {}
