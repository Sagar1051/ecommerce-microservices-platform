package com.ecommerce.order.client;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mirrors cart-service's CartResponse/CartItem shape. Kept as a separate copy
 * here rather than a shared library — each service should be independently
 * deployable without a shared-model dependency between them.
 */
public record CartClientResponse(
        List<CartItemDto> items,
        double total
) {
    public record CartItemDto(
            Long productId,
            String productName,
            BigDecimal price,
            Integer quantity
    ) {}
}
