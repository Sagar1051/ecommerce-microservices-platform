package com.ecommerce.recommendation.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI recommendationServiceApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Recommendation Service API")
                        .description("AI-powered recommendation engine. Kafka-driven collaborative " +
                                "filtering, re-ranked and explained by an LLM (Groq / Llama 3.1).")
                        .version("1.0.0"));
    }
}
