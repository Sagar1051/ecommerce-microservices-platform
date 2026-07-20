package com.ecommerce.order.exception;

public class EmptyCartException extends RuntimeException {

    public EmptyCartException(Long userId) {
        super("Cannot place an order — cart is empty for userId: " + userId);
    }

}