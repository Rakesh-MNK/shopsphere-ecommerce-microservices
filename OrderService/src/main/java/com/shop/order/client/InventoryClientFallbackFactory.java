package com.shop.order.client;

import com.shop.order.dto.StockRequest;
import com.shop.order.dto.StockResponse;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class InventoryClientFallbackFactory implements FallbackFactory<InventoryClient> {

    @Override
    public InventoryClient create(Throwable cause) {
        return new InventoryClient() {
            @Override
            public StockResponse reserve(StockRequest request, String authorization) {
                throw FeignErrors.map(cause, "inventory-service");
            }

            @Override
            public StockResponse release(StockRequest request, String authorization) {
                throw FeignErrors.map(cause, "inventory-service");
            }

            @Override
            public StockResponse restock(StockRequest request, String authorization) {
                throw FeignErrors.map(cause, "inventory-service");
            }
        };
    }
}
