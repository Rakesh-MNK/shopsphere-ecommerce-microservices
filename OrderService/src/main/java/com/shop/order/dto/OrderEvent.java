package com.shop.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** eventType: ORDER_CREATED | ORDER_CONFIRMED | ORDER_PAYMENT_FAILED | ORDER_CANCELLED */
public record OrderEvent(String eventType, Long orderId, Long userId, String status, BigDecimal totalAmount,
                         LocalDateTime timestamp) {
}
