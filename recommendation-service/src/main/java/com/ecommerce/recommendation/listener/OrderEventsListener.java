package com.ecommerce.recommendation.listener;

import com.ecommerce.recommendation.entity.InteractionType;
import com.ecommerce.recommendation.entity.UserInteraction;
import com.ecommerce.recommendation.event.OrderPlacedEvent;
import com.ecommerce.recommendation.repository.UserInteractionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventsListener {

    private final UserInteractionRepository interactionRepository;

    @KafkaListener(
            topics = "order-events",
            groupId = "recommendation-service-order-events",
            containerFactory = "orderPlacedListenerFactory"
    )
    public void onOrderPlaced(OrderPlacedEvent event) {
        for (OrderPlacedEvent.OrderedItem item : event.items()) {
            UserInteraction interaction = UserInteraction.builder()
                    .userId(event.userId())
                    .productId(item.productId())
                    .type(InteractionType.PURCHASE)
                    .build();
            interactionRepository.save(interaction);
        }
        log.debug("Recorded PURCHASE interactions for orderId={} userId={} ({} items)",
                event.orderId(), event.userId(), event.items().size());
    }
}
