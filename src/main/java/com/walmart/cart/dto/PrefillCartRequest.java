package com.walmart.cart.dto;

import jakarta.validation.constraints.NotNull;

public record PrefillCartRequest(@NotNull Long userId) {
}
