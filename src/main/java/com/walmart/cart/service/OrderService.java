package com.walmart.cart.service;

import com.walmart.cart.dto.OrderItemRequest;
import com.walmart.cart.dto.OrderTrackingResponse;
import com.walmart.cart.model.*;
import com.walmart.cart.repository.CartRepository;
import com.walmart.cart.repository.OrderRepository;
import com.walmart.cart.repository.ProductRepository;
import com.walmart.cart.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Order placeOrder(Long userId, java.util.List<OrderItemRequest> items) {
        User user = resolveUser(userId);

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder()
                        .user(user)
                        .preFilled(false)
                        .build()));

        cart.getItems().clear();

        BigDecimal total = items.stream()
                .map(reqItem -> {
                    Product product = productRepository.findById(reqItem.productId())
                            .orElseThrow(() -> new IllegalArgumentException("Product not found: " + reqItem.productId()));
                    CartItem cartItem = CartItem.builder()
                            .cart(cart)
                            .product(product)
                            .quantity(reqItem.quantity())
                            .build();
                    cart.getItems().add(cartItem);
                    return product.getPrice().multiply(BigDecimal.valueOf(reqItem.quantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cartRepository.save(cart);

        LocalDateTime now = LocalDateTime.now();
        Order order = Order.builder()
                .customer(user)
                .status(OrderStatus.PLACED)
                .totalAmount(total)
                .orderPlacementTime(now)
                .estimatedCompletionTime(now.plusMinutes(60))
                .build();

        return orderRepository.save(order);
    }


    private User resolveUser(Long userId) {
        if (userId == null || userId <= 0) {
            return userRepository.findAll().stream()
                    .min(Comparator.comparing(User::getId))
                    .orElseThrow(() -> new IllegalArgumentException("No users available. Seed data may be missing."));
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId + ". Use an existing userId or 0 to use default seeded user."));
    }

    public OrderTrackingResponse trackOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        long elapsedMinutes = Duration.between(order.getOrderPlacementTime(), LocalDateTime.now()).toMinutes();
        OrderStatus computedStatus;
        String stage;
        if (elapsedMinutes < 20) {
            computedStatus = OrderStatus.PLACED;
            stage = "Order Placed";
        } else if (elapsedMinutes < 45) {
            computedStatus = OrderStatus.PICKING_ESSENTIALS;
            stage = "Picking Essentials";
        } else if (elapsedMinutes < 60) {
            computedStatus = OrderStatus.READY_FOR_PICKUP;
            stage = "Ready for Pickup";
        } else {
            computedStatus = OrderStatus.DELIVERED;
            stage = "Delivered";
        }

        if (order.getStatus() != computedStatus) {
            order.setStatus(computedStatus);
            orderRepository.save(order);
        }

        long remainingSeconds = Math.max(0, Duration.between(LocalDateTime.now(), order.getEstimatedCompletionTime()).getSeconds());
        return new OrderTrackingResponse(order.getId(), computedStatus, stage, order.getEstimatedCompletionTime(), remainingSeconds);
    }
}
