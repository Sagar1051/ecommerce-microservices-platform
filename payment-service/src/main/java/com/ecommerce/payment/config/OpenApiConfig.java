package com.ecommerce.payment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI paymentServiceApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Payment Service API")
                        .description("Payment processing service. Mock gateway simulating realistic " +
                                "success/failure rates per payment method.")
                        .version("1.0.0"));
    }
}
