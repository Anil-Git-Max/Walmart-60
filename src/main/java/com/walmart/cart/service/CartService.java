package com.walmart.cart.service;

import com.walmart.cart.dto.CartItemRequest;
import com.walmart.cart.model.*;
import com.walmart.cart.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public Cart preFillCart(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).preFilled(true).build()));

        cart.setPreFilled(true);
        List<Product> essentials = productRepository.findByEssentialTrue();
        for (Product product : essentials) {
            cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                    .orElseGet(() -> cartItemRepository.save(CartItem.builder()
                            .cart(cart)
                            .product(product)
                            .quantity(1)
                            .build()));
        }
        return cartRepository.findById(cart.getId()).orElseThrow();
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
