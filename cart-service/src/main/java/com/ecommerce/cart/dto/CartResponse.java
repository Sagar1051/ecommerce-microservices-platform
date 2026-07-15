package com.ecommerce.cart.dto;

import com.ecommerce.cart.model.CartItem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
public record CartResponse(
        @Schema(description = "Items currently in the cart")
        List<CartItem> items,

        @Schema(example = "1598.0", description = "Sum of price * quantity across all items")
        double total
) {}
