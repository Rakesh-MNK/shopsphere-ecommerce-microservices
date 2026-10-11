package com.shop.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** eventType: PAYMENT_SUCCESS | PAYMENT_FAILED */
public record PaymentEvent(String eventType, Long paymentId, Long orderId, Long userId, BigDecimal amount,
                           String status, String reason, LocalDateTime timestamp, String mail) {
}
