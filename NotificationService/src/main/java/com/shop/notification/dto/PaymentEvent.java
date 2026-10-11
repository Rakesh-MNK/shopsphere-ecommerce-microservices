package com.shop.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentEvent(String eventType, Long paymentId, Long orderId, Long userId, BigDecimal amount,
                           String status, String reason,String mail) {
}
