package com.walmart.cart.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Walmart 60-Minute Cart API")
                        .version("v1")
                        .description("APIs for product discovery, cart management, and order tracking."))
                .tags(List.of(
                        new Tag().name("1. Product APIs").description("Step 1: Discover essentials products."),
                        new Tag().name("2. Cart APIs").description("Step 2: Pre-fill and update cart items."),
                        new Tag().name("3. Order APIs").description("Step 3: Place and track 60-minute orders.")
                ));
    }
}
