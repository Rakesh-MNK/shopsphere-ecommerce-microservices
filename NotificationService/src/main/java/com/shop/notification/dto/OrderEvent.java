package com.shop.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderEvent(String eventType, Long orderId, Long userId, String status, BigDecimal totalAmount) {
}
