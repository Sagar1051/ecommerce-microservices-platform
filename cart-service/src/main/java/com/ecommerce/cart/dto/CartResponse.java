package com.ecommerce.cart.dto;

import com.ecommerce.cart.model.CartItem;
import lombok.Builder;

import java.util.List;

@Builder
public record CartResponse(
        List<CartItem> items,
        double total
) {}
