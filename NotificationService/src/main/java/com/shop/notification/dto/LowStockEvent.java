package com.shop.notification.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LowStockEvent(String eventType, Long productId, String productName, int availableQty, int threshold) {
}
