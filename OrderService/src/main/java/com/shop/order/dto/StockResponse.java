package com.shop.order.dto;

public record StockResponse(Long productId, String productName, int availableQty, int reservedQty) {
}
