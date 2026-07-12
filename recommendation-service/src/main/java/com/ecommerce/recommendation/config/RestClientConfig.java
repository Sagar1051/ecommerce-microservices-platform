package com.ecommerce.recommendation.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestClientConfig {

    @Bean
    @Primary
    @LoadBalanced
    public RestTemplate loadBalancedRestTemplate() {
        return new RestTemplate();
    }

    /**
     * A second, non-load-balanced RestTemplate for calling external APIs
     * (Anthropic) that aren't registered with Eureka.
     */
    @Bean
    public RestTemplate plainRestTemplate() {
        return new RestTemplate();
    }
}
