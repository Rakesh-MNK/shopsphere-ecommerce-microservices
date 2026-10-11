package com.shop.order.client;

import com.shop.order.dto.PaymentRequest;
import com.shop.order.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "payment-service", fallbackFactory = PaymentClientFallbackFactory.class)
public interface PaymentClient {

    @PostMapping("/api/payments")
    PaymentResponse pay(@RequestBody PaymentRequest request, @RequestHeader("Authorization") String authorization,@RequestParam String mail);
}
