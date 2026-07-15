package com.ecommerce.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddToCartRequest(
        @NotNull(message = "Product ID is required")
        @Schema(example = "1", description = "Product id")
        Long productId,

        @NotBlank(message = "Product name is required")
        @Schema(example = "Wireless Mouse", description = "Product name, shown in the cart")
        String productName,

        @NotNull(message = "Price is required")
        @Schema(example = "799.00", description = "Price at the time it was added to the cart")
        Double price,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        @Schema(example = "2", description = "Number of units to add")
        Integer quantity
) {}
