package com.shop.order.client;

import com.shop.order.dto.PaymentRequest;
import com.shop.order.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class PaymentClientFallbackFactory implements FallbackFactory<PaymentClient> {

    @Override
    public PaymentClient create(Throwable cause) {
        return new PaymentClient() {
            @Override
            public PaymentResponse pay(PaymentRequest request, String authorization, String mail) {
                throw FeignErrors.map(cause, "payment-service");
            }
        };
    }
}
