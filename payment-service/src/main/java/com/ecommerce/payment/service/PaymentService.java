package com.ecommerce.payment.service;

import com.ecommerce.payment.dto.PaymentRequest;
import com.ecommerce.payment.dto.PaymentResponse;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.entity.PaymentMethod;
import com.ecommerce.payment.exception.InvalidPaymentMethodException;
import com.ecommerce.payment.exception.PaymentAlreadyExistsException;
import com.ecommerce.payment.exception.PaymentNotFoundException;
import com.ecommerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MockPaymentGateway paymentGateway;
    private final PaymentEventPublisher eventPublisher;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        if (paymentRepository.findByOrderId(request.orderId()).isPresent()) {
            throw new PaymentAlreadyExistsException(request.orderId());
        }

        PaymentMethod method = parseMethod(request.method());
        MockPaymentGateway.GatewayResult result = paymentGateway.charge(method);

        Payment payment = Payment.builder()
                .orderId(request.orderId())
                .userId(request.userId())
                .amount(request.amount())
                .method(method)
                .status(result.status())
                .failureReason(result.failureReason())
                .build();

        Payment saved = paymentRepository.save(payment);
        eventPublisher.publishPaymentProcessed(saved);

        return toResponse(saved);
    }

    public PaymentResponse findByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PaymentNotFoundException(orderId));
        return toResponse(payment);
    }

    private PaymentMethod parseMethod(String raw) {
        try {
            return PaymentMethod.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidPaymentMethodException(raw);
        }
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .method(payment.getMethod().name())
                .status(payment.getStatus().name())
                .transactionId(payment.getTransactionId())
                .failureReason(payment.getFailureReason())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
