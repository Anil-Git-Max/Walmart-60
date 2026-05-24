package com.walmart.cart.service;

import com.walmart.cart.dto.CartItemRequest;
import com.walmart.cart.dto.PrefillCartItemRequest;
import com.walmart.cart.model.*;
import com.walmart.cart.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public Cart preFillCart(Long userId, List<PrefillCartItemRequest> requestedItems) {
        User user = resolveUser(userId);

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).preFilled(true).build()));

        cart.setPreFilled(true);
        List<PrefillCartItemRequest> validRequestedItems = requestedItems == null ? List.of() :
                requestedItems.stream()
                        .filter(item -> item.productId() != null && item.productId() > 0)
                        .toList();

        if (!validRequestedItems.isEmpty()) {
            for (PrefillCartItemRequest item : validRequestedItems) {
                Product product = productRepository.findById(item.productId())
                        .orElseThrow(() -> new IllegalArgumentException("Product not found: " + item.productId()));
                CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                        .orElseGet(() -> CartItem.builder()
                                .cart(cart)
                                .product(product)
                                .quantity(item.quantity())
                                .build());
                cartItem.setQuantity(item.quantity());
                cartItemRepository.save(cartItem);
            }
        } else {
            List<Product> essentials = productRepository.findByEssentialTrue();
            for (Product product : essentials) {
                cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                        .orElseGet(() -> cartItemRepository.save(CartItem.builder()
                                .cart(cart)
                                .product(product)
                                .quantity(1)
                                .build()));
            }
        }
        return cartRepository.findById(cart.getId()).orElseThrow();
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

    @Transactional
    public Cart addOrUpdateCartItem(CartItemRequest request) {
        Cart cart = cartRepository.findByUserId(request.userId())
                .orElseGet(() -> {
                    User user = userRepository.findById(request.userId())
                            .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.userId()));
                    return cartRepository.save(Cart.builder().user(user).preFilled(false).build());
                });

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + request.productId()));

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .map(existing -> {
                    existing.setQuantity(request.quantity());
                    return existing;
                })
                .orElseGet(() -> CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(request.quantity())
                        .build());

        cartItemRepository.save(item);
        return cartRepository.findById(cart.getId()).orElseThrow();
    }
}
