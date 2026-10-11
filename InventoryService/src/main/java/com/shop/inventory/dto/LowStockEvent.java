package com.shop.inventory.dto;

import java.time.LocalDateTime;

public record LowStockEvent(String eventType, Long productId, String productName, int availableQty,
                            int threshold, LocalDateTime timestamp) {
}
