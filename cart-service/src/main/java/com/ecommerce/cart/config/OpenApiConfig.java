package com.ecommerce.cart.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cartServiceApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cart Service API")
                        .description("Redis-backed shopping cart service.")
                        .version("1.0.0"));
    }
}
