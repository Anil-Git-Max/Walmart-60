package com.walmart.cart.repository;

import com.walmart.cart.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByEssentialTrue();
}
