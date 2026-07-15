package com.ecommerce.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record OrderResponse(
        @Schema(example = "1", description = "Order id")
        Long id,

        @Schema(example = "1", description = "Id of the user who placed the order")
        Long userId,

        @Schema(description = "Line items copied from the cart at order time")
        List<OrderItemResponse> items,

        @Schema(example = "3196.00", description = "Total order amount")
        BigDecimal totalAmount,

        @Schema(example = "CONFIRMED", description = "Order status")
        String status,

        @Schema(description = "When the order was placed")
        Instant createdAt
) {
    @Builder
    public record OrderItemResponse(
            @Schema(example = "1", description = "Product id")
            Long productId,

            @Schema(example = "Wireless Mouse", description = "Product name at order time")
            String productName,

            @Schema(example = "799.00", description = "Price at order time, not the live product price")
            BigDecimal price,

            @Schema(example = "4", description = "Quantity ordered")
            Integer quantity
    ) {}
}
