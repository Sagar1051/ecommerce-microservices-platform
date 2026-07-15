package com.ecommerce.auth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration

public class OpenApiConfig {
    @Bean
    public OpenAPI authServiceAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auth Service API")
                        .description("""
                                Authentication Microservice

                                Features:
                                • User Registration
                                • User Login
                                • JWT Authentication
                                • BCrypt Password Encryption
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Sagar Malgave")
                                .url("https://github.com/Sagar1051"))
                        .license(new License()
                                .name("MIT License")));
    }
}
