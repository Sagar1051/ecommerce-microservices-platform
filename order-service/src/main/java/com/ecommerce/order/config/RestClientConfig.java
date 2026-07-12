package com.ecommerce.order.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {

    /**
     * @LoadBalanced lets us call http://cart-service/... using the service's
     * Eureka name instead of a hardcoded host:port. Spring Cloud LoadBalancer
     * resolves "cart-service" to whatever instance(s) are currently registered.
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
