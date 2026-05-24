package com.walmart.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PrefillCartRequest(
        @Schema(example = "1") @NotNull Long userId,
        List<@Valid PrefillCartItemRequest> items
) {
}
