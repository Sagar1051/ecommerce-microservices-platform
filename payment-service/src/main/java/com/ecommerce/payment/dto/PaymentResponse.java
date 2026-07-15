package com.ecommerce.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record PaymentResponse(
        @Schema(example = "1", description = "Payment id")
        Long id,

        @Schema(example = "1", description = "Id of the order this payment is for")
        Long orderId,

        @Schema(example = "1", description = "Id of the user who paid")
        Long userId,

        @Schema(example = "3196.00", description = "Amount charged")
        BigDecimal amount,

        @Schema(example = "CARD", description = "Payment method used")
        String method,

        @Schema(example = "SUCCESS", description = "SUCCESS or FAILED")
        String status,

        @Schema(example = "TXN-A1B2C3D4E5F6", description = "Mock gateway transaction id")
        String transactionId,

        @Schema(example = "Card declined by issuing bank", description = "Populated only when status is FAILED")
        String failureReason,

        @Schema(description = "When the payment was processed")
        Instant createdAt
) {}
