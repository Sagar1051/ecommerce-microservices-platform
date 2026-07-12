package com.ecommerce.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddToCartRequest(
        @NotNull(message = "Product ID is required")
        Long productId,

        @NotBlank(message = "Product name is required")
        String productName,

        @NotNull(message = "Price is required")
        Double price,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
) {}
