package com.walmart.cart.dto;

import com.walmart.cart.model.OrderStatus;

import java.time.LocalDateTime;

public record OrderTrackingResponse(
        Long orderId,
        OrderStatus status,
        String trackerStage,
        LocalDateTime estimatedCompletionTime,
        long remainingSeconds
) {
}
