package com.ecommerce.recommendation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record RecommendationResponse(
        @Schema(example = "1", description = "Id of the user these recommendations are for")
        Long userId,

        @Schema(description = "Recommended products, best match first")
        List<RecommendationItem> recommendations,

        @Schema(example = "true", description = "true if Groq re-ranked and explained these; " +
                "false if the system fell back to plain collaborative filtering")
        boolean aiRanked
) {
    @Builder
    public record RecommendationItem(
            @Schema(example = "4", description = "Product id")
            Long productId,

            @Schema(example = "USB-C Hub", description = "Product name")
            String name,

            @Schema(example = "Accessories", description = "Product category")
            String category,

            @Schema(example = "1899.00", description = "Product price")
            java.math.BigDecimal price,

            @Schema(example = "Complementary to wireless devices with USB-C connectivity",
                    description = "Why this was recommended — from Groq when aiRanked is true, generic otherwise")
            String reason
    ) {}
}
