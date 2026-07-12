package com.ecommerce.payment.event;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Published to Kafka topic "payment-events". order-service could subscribe
 * to this to flip an order from PENDING to CONFIRMED/CANCELLED based on the
 * outcome — not wired up yet in order-service, but this is where that
 * integration would plug in.
 */
@Builder
public record PaymentProcessedEvent(
        Long paymentId,
        Long orderId,
        Long userId,
        BigDecimal amount,
        String status,
        Instant processedAt
) {}
