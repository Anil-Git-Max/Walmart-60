package com.walmart.cart.controller;

import com.walmart.cart.dto.CreateOrderRequest;
import com.walmart.cart.dto.OrderTrackingResponse;
import com.walmart.cart.model.Order;
import com.walmart.cart.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Checkout the cart and place a 60-minute order")
    @ApiResponse(responseCode = "201", description = "Order placed successfully")
    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody @Valid CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(request.userId()));
    }

    @Operation(summary = "Track order status for 60-minute tracker")
    @ApiResponse(responseCode = "200", description = "Order tracking details returned")
    @GetMapping("/{id}/track")
    public ResponseEntity<OrderTrackingResponse> trackOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.trackOrder(id));
    }
}
