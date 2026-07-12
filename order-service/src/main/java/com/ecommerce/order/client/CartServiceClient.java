package com.ecommerce.order.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class CartServiceClient {

    private final RestTemplate restTemplate;

    public CartClientResponse getCart(Long userId) {
        // "cart-service" is resolved via Eureka by the @LoadBalanced RestTemplate —
        // no hardcoded host or port here.
        String url = "http://cart-service/api/cart/" + userId;
        return restTemplate.getForObject(url, CartClientResponse.class);
    }

    public void clearCart(Long userId) {
        String url = "http://cart-service/api/cart/" + userId + "/clear";
        restTemplate.delete(url);
    }
}
