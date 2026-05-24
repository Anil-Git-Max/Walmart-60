package com.walmart.cart.controller;

import com.walmart.cart.dto.CartItemRequest;
import com.walmart.cart.dto.PrefillCartRequest;
import com.walmart.cart.model.Cart;
import com.walmart.cart.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Auto-populate cart with essentials")
    @ApiResponse(responseCode = "201", description = "Cart pre-filled successfully")
    @PostMapping("/pre-fill")
    public ResponseEntity<Cart> preFillCart(@RequestBody @Valid PrefillCartRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.preFillCart(request.userId()));
    }

    @Operation(summary = "Add or update a cart item")
    @ApiResponse(responseCode = "200", description = "Cart item updated successfully")
    @PostMapping("/items")
    public ResponseEntity<Cart> addOrUpdateItem(@RequestBody @Valid CartItemRequest request) {
        return ResponseEntity.ok(cartService.addOrUpdateCartItem(request));
    }
}
