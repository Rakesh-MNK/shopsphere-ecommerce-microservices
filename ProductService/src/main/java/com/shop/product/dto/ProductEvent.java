package com.shop.product.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** eventType: PRODUCT_CREATED | PRODUCT_UPDATED | PRODUCT_DELETED */
public record ProductEvent(String eventType, Long productId, String productName, BigDecimal price,
                           Integer initialStock, LocalDateTime timestamp) {
}
