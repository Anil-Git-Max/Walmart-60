package com.walmart.cart.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PrefillCartRequest(
        @NotNull Long userId,
        List<@Valid PrefillCartItemRequest> items
) {
}
