package com.ecommerce.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull(message = "Order ID is required")
        Long orderId,

        @NotNull(message = "User ID is required")
        Long userId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
        BigDecimal amount,

        @NotNull(message = "Payment method is required")
        String method
) {}
