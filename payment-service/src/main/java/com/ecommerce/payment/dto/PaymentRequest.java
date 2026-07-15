package com.ecommerce.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull(message = "Order ID is required")
        @Schema(example = "1", description = "Id of the order being paid for")
        Long orderId,

        @NotNull(message = "User ID is required")
        @Schema(example = "1", description = "Id of the user making the payment")
        Long userId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
        @Schema(example = "3196.00", description = "Amount to charge")
        BigDecimal amount,

        @NotNull(message = "Payment method is required")
        @Schema(example = "CARD", description = "One of CARD, UPI, COD")
        String method
) {}
