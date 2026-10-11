package com.shop.payment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StockItem(
        @NotNull Long productId,
        @NotNull @Min(1) Integer quantity) {
}
