package com.shop.product.dto;

import com.shop.product.entity.Product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(Long id, String name, String description, BigDecimal price,
                              String category, LocalDateTime createdAt, LocalDateTime updatedAt) {

    public static ProductResponse from(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getDescription(), p.getPrice(),
                p.getCategory(), p.getCreatedAt(), p.getUpdatedAt());
    }
}
