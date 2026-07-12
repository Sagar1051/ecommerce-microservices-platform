package com.ecommerce.recommendation.listener;

import com.ecommerce.recommendation.entity.InteractionType;
import com.ecommerce.recommendation.entity.UserInteraction;
import com.ecommerce.recommendation.event.ProductViewedEvent;
import com.ecommerce.recommendation.repository.UserInteractionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventsListener {

    private final UserInteractionRepository interactionRepository;

    @KafkaListener(
            topics = "product-events",
            groupId = "recommendation-service-product-events",
            containerFactory = "productViewedListenerFactory"
    )
    public void onProductViewed(ProductViewedEvent event) {
        // Anonymous views (no logged-in user) don't help build a profile —
        // nothing to attach the signal to.
        if (event.userId() == null) {
            return;
        }

        UserInteraction interaction = UserInteraction.builder()
                .userId(event.userId())
                .productId(event.productId())
                .type(InteractionType.VIEW)
                .build();

        interactionRepository.save(interaction);
        log.debug("Recorded VIEW: userId={} productId={}", event.userId(), event.productId());
    }
}
