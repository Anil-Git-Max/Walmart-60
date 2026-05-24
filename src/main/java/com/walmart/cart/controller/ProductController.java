package com.walmart.cart.controller;

import com.walmart.cart.model.Product;
import com.walmart.cart.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "Fetch essential/recommended products")
    @ApiResponse(responseCode = "200", description = "Essential products fetched successfully")
    @GetMapping("/essentials")
    public ResponseEntity<List<Product>> getEssentials() {
        return ResponseEntity.ok(productService.getEssentialProducts());
    }
}
