package com.walmart.cart.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PrefillCartItemRequest(
        @NotNull Long productId,
        @NotNull @Min(1) Integer quantity
) {
}
