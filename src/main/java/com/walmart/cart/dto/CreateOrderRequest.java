package com.walmart.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CreateOrderRequest(
        @Schema(example = "1") @NotNull Long userId,
        @Schema(example = "1") @NotNull Long cartId
) {
}
