package com.ecommerce.payment.dto;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

@Builder
public record PaymentResponse(
        Long id,
        Long orderId,
        Long userId,
        BigDecimal amount,
        String method,
        String status,
        String transactionId,
        String failureReason,
        Instant createdAt
) {}
