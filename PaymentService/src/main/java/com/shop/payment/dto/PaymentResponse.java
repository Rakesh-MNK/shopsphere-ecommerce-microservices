package com.shop.payment.dto;

import com.shop.payment.entity.Payment;

import java.math.BigDecimal;

public record PaymentResponse(Long paymentId, Long orderId, String status, String transactionRef,
                              BigDecimal amount, String message) {

    public static PaymentResponse from(Payment p, String message) {
        return new PaymentResponse(p.getId(), p.getOrderId(), p.getStatus().name(), p.getTransactionRef(),
                p.getAmount(), message);
    }
}
