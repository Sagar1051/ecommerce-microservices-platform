package com.ecommerce.product.service;

import com.ecommerce.product.config.KafkaTopicConfig;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.event.ProductViewedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishViewedEvent(Product product, Long userId) {
        ProductViewedEvent event = ProductViewedEvent.builder()
                .productId(product.getId())
                .productName(product.getName())
                .category(product.getCategory())
                .userId(userId)
                .viewedAt(Instant.now())
                .build();

        // Keyed by product id so all events for the same product land on the
        // same partition, preserving order for downstream consumers.
        kafkaTemplate.send(KafkaTopicConfig.PRODUCT_EVENTS_TOPIC, product.getId().toString(), event);
        log.debug("Published ProductViewedEvent for productId={} userId={}", product.getId(), userId);
    }
}
