package com.ecommerce.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Name is required")
        @Schema(example = "Wireless Mouse", description = "Product name")
        String name,

        @Schema(example = "Ergonomic wireless mouse with USB receiver", description = "Product description")
        String description,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
        @Schema(example = "799.00", description = "Price in the platform's base currency")
        BigDecimal price,

        @NotBlank(message = "Category is required")
        @Schema(example = "Electronics", description = "Product category, used for filtering")
        String category,

        @NotNull(message = "Stock quantity is required")
        @Min(value = 0, message = "Stock quantity cannot be negative")
        @Schema(example = "50", description = "Units currently in stock")
        Integer stockQuantity,

        @Schema(example = "https://example.com/images/mouse.jpg", description = "Product image URL")
        String imageUrl
) {}
