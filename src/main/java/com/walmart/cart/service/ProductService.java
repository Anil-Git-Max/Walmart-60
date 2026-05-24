package com.walmart.cart.service;

import com.walmart.cart.model.Product;
import com.walmart.cart.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getEssentialProducts() {
        return productRepository.findByEssentialTrue();
    }
}
