package com.shop.order.dto;

import java.math.BigDecimal;

public record PaymentResponse(Long paymentId, Long orderId, String status, String transactionRef,
                              BigDecimal amount, String message) {
}
