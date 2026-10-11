package com.shop.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record CreateOrderRequest(@NotEmpty(message = "At least one item is required") List<@Valid Item> items,
		@NotBlank(message = "Payment method is required") String paymentMethod, String cardNumber) {

	public record Item(@NotNull(message = "productId is required") Long productId,
			@NotNull(message = "quantity is required") @Min(value = 1, message = "quantity must be at least 1") Integer quantity) {
	}
}
