package com.ecommerce.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record ProductResponse(
        @Schema(example = "1", description = "Product id")
        Long id,

        @Schema(example = "Wireless Mouse", description = "Product name")
        String name,

        @Schema(example = "Ergonomic wireless mouse with USB receiver", description = "Product description")
        String description,

        @Schema(example = "799.00", description = "Price in the platform's base currency")
        BigDecimal price,

        @Schema(example = "Electronics", description = "Product category")
        String category,

        @Schema(example = "50", description = "Units currently in stock")
        Integer stockQuantity,

        @Schema(example = "https://example.com/images/mouse.jpg", description = "Product image URL")
        String imageUrl,

        @Schema(description = "When the product was created")
        Instant createdAt,

        @Schema(description = "When the product was last updated")
        Instant updatedAt
) {}
