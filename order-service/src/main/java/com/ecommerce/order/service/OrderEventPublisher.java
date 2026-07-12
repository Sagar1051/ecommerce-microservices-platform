package com.ecommerce.order.service;

import com.ecommerce.order.config.KafkaTopicConfig;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.event.OrderPlacedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrderPlaced(Order order) {
        List<OrderPlacedEvent.OrderedItem> items = order.getItems().stream()
                .map(this::toOrderedItem)
                .toList();

        OrderPlacedEvent event = OrderPlacedEvent.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .items(items)
                .totalAmount(order.getTotalAmount())
                .placedAt(Instant.now())
                .build();

        kafkaTemplate.send(KafkaTopicConfig.ORDER_EVENTS_TOPIC, order.getUserId().toString(), event);
        log.debug("Published OrderPlacedEvent for orderId={} userId={}", order.getId(), order.getUserId());
    }

    private OrderPlacedEvent.OrderedItem toOrderedItem(OrderItem item) {
        return new OrderPlacedEvent.OrderedItem(item.getProductId(), item.getQuantity());
    }
}
