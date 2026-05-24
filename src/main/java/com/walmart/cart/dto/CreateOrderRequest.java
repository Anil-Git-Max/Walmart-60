package com.walmart.cart.dto;

import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(@NotNull Long userId) {
}
