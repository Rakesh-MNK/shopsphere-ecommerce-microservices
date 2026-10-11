package com.shop.payment.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record PaymentRequest(
        @NotNull(message = "orderId is required") Long orderId,
        @NotNull(message = "userId is required") Long userId,
        @NotNull(message = "amount is required") @DecimalMin("0.01") BigDecimal amount,
        @NotBlank(message = "payment method is required") String method,
        String cardNumber,
        @NotEmpty(message = "items are required") List<@Valid StockItem> items) {
}
