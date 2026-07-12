package com.ecommerce.payment.exception;

public class PaymentAlreadyExistsException extends RuntimeException {
    public PaymentAlreadyExistsException(Long orderId) {
        super("A payment already exists for orderId: " + orderId);
    }
}
