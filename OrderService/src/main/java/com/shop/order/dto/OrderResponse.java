package com.shop.order.dto;

import com.shop.order.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(Long id, Long userId, String status, BigDecimal totalAmount, Long paymentId,
                            String paymentStatus, LocalDateTime createdAt, List<ItemResponse> items) {

    public record ItemResponse(Long productId, String productName, BigDecimal unitPrice, int quantity, BigDecimal subtotal) {
    }

    public static OrderResponse from(Order o) {
        List<ItemResponse> items = o.getItems().stream()
                .map(i -> new ItemResponse(i.getProductId(), i.getProductName(), i.getUnitPrice(), i.getQuantity(), i.getSubtotal()))
                .toList();
        return new OrderResponse(o.getId(), o.getUserId(), o.getStatus().name(), o.getTotalAmount(),
                o.getPaymentId(), o.getPaymentStatus(), o.getCreatedAt(), items);
    }
}
