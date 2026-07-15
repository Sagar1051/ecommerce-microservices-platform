package com.ecommerce.order.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI orderServiceApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Order Service API")
                        .description("Order processing service. Reads the cart via service-to-service " +
                                "calls, persists the order, and publishes order events to Kafka.")
                        .version("1.0.0"));
    }
}
