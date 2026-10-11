package com.shop.inventory.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProductEvent(String eventType, Long productId, String productName, BigDecimal price, Integer initialStock) {
}
