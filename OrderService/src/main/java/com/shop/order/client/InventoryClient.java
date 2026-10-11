package com.shop.order.client;

import com.shop.order.dto.StockRequest;
import com.shop.order.dto.StockResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "inventory-service", fallbackFactory = InventoryClientFallbackFactory.class)
public interface InventoryClient {

    @PostMapping("/api/inventory/reserve")
    StockResponse reserve(@RequestBody StockRequest request, @RequestHeader("Authorization") String authorization);

    @PostMapping("/api/inventory/release")
    StockResponse release(@RequestBody StockRequest request, @RequestHeader("Authorization") String authorization);

    @PostMapping("/api/inventory/restock")
    StockResponse restock(@RequestBody StockRequest request, @RequestHeader("Authorization") String authorization);
}
