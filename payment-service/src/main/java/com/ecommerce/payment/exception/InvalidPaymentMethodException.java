package com.ecommerce.payment.exception;

public class InvalidPaymentMethodException extends RuntimeException {
    public InvalidPaymentMethodException(String method) {
        super("Invalid payment method: " + method + ". Must be one of CARD, UPI, COD");
    }
}
