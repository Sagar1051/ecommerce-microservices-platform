package com.ecommerce.order.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Builder
public record OrderResponse(
        Long id,
        Long userId,
        List<OrderItemResponse> items,
        BigDecimal totalAmount,
        String status,
        Instant createdAt
) {
    @Builder
    public record OrderItemResponse(
            Long productId,
            String productName,
            BigDecimal price,
            Integer quantity
    ) {}
}
