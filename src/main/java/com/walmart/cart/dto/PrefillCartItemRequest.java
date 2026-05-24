package com.walmart.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PrefillCartItemRequest(
        @Schema(example = "1") @NotNull Long productId,
        @Schema(example = "1") @NotNull @Min(1) Integer quantity
) {
}
