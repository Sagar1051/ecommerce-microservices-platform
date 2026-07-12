package com.ecommerce.payment.exception;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(Long orderId) {
        super("No payment found for orderId: " + orderId);
    }
}
