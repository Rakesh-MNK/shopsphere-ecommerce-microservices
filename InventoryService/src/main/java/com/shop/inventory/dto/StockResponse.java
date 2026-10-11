package com.shop.inventory.dto;

import com.shop.inventory.entity.Inventory;

public record StockResponse(Long productId, String productName, int availableQty, int reservedQty) {

    public static StockResponse from(Inventory i) {
        return new StockResponse(i.getProductId(), i.getProductName(), i.getAvailableQty(), i.getReservedQty());
    }
}
