package com.walmart.cart.config;

import com.walmart.cart.model.Product;
import com.walmart.cart.model.User;
import com.walmart.cart.repository.ProductRepository;
import com.walmart.cart.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedData(ProductRepository productRepository, UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.save(User.builder()
                        .firstName("Alex")
                        .lastName("Morgan")
                        .email("alex.morgan@example.com")
                        .build());
            }

            if (productRepository.count() == 0) {
                productRepository.saveAll(List.of(
                        Product.builder().name("Cage-Free Eggs, Dozen")
                                .imageUrl("https://example.com/images/eggs.jpg")
                                .price(new BigDecimal("3.94"))
                                .rollback(true)
                                .categoryTag("Dairy & Eggs")
                                .essential(true).build(),
                        Product.builder().name("Whole Milk, 1/2 Gal")
                                .imageUrl("https://example.com/images/milk.jpg")
                                .price(new BigDecimal("2.45"))
                                .rollback(false)
                                .categoryTag("Dairy & Eggs")
                                .essential(true).build(),
                        Product.builder().name("Fresh Bananas, Bunch")
                                .imageUrl("https://example.com/images/bananas.jpg")
                                .price(new BigDecimal("1.58"))
                                .rollback(false)
                                .categoryTag("Produce")
                                .essential(true).build(),
                        Product.builder().name("Ground Coffee, 12 oz")
                                .imageUrl("https://example.com/images/coffee.jpg")
                                .price(new BigDecimal("7.99"))
                                .rollback(false)
                                .categoryTag("Pantry")
                                .essential(false).build()
                ));
            }
        };
    }
}
