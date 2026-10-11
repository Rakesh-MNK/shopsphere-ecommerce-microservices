package com.shop.order.dto;

import java.math.BigDecimal;
import java.util.List;

public record PaymentRequest(Long orderId, Long userId, BigDecimal amount, String method, String cardNumber,
                             List<StockRequest> items) {
}
