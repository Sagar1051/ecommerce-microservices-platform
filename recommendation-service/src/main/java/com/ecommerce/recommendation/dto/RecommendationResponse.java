package com.ecommerce.recommendation.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record RecommendationResponse(
        Long userId,
        List<RecommendationItem> recommendations,
        boolean aiRanked
) {
    @Builder
    public record RecommendationItem(
            Long productId,
            String name,
            String category,
            java.math.BigDecimal price,
            String reason
    ) {}
}
