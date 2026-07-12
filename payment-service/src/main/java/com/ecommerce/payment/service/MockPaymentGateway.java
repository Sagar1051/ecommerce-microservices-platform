package com.ecommerce.payment.service;

import com.ecommerce.payment.entity.PaymentMethod;
import com.ecommerce.payment.entity.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

/**
 * Stands in for a real gateway (Razorpay/Stripe) so the rest of the system
 * can be built and demoed without needing production payment credentials.
 * Swapping this out for a real SDK call later wouldn't require touching
 * PaymentService — only this class.
 */
@Component
public class MockPaymentGateway {

    private static final Random RANDOM = new Random();
    private static final double CARD_SUCCESS_RATE = 0.9;
    private static final double UPI_SUCCESS_RATE = 0.95;

    private static final List<String> FAILURE_REASONS = List.of(
            "Insufficient funds",
            "Card declined by issuing bank",
            "Payment gateway timeout",
            "Transaction limit exceeded"
    );

    public GatewayResult charge(PaymentMethod method) {
        // COD has nothing to "charge" right now — it's always accepted, the
        // actual cash exchange happens on delivery.
        if (method == PaymentMethod.COD) {
            return new GatewayResult(PaymentStatus.SUCCESS, null);
        }

        double successRate = method == PaymentMethod.CARD ? CARD_SUCCESS_RATE : UPI_SUCCESS_RATE;
        boolean succeeded = RANDOM.nextDouble() < successRate;

        if (succeeded) {
            return new GatewayResult(PaymentStatus.SUCCESS, null);
        }

        String reason = FAILURE_REASONS.get(RANDOM.nextInt(FAILURE_REASONS.size()));
        return new GatewayResult(PaymentStatus.FAILED, reason);
    }

    public record GatewayResult(PaymentStatus status, String failureReason) {}
}
