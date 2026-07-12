package com.ecommerce.payment.service;

import com.ecommerce.payment.config.KafkaTopicConfig;
import com.ecommerce.payment.entity.Payment;
import com.ecommerce.payment.event.PaymentProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishPaymentProcessed(Payment payment) {
        PaymentProcessedEvent event = PaymentProcessedEvent.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .processedAt(Instant.now())
                .build();

        kafkaTemplate.send(KafkaTopicConfig.PAYMENT_EVENTS_TOPIC, payment.getOrderId().toString(), event);
        log.debug("Published PaymentProcessedEvent for orderId={} status={}", payment.getOrderId(), payment.getStatus());
    }
}
