package com.shop.payment.client;

import com.shop.payment.dto.StockItem;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "inventory-service", fallbackFactory = InventoryClientFallbackFactory.class)
public interface InventoryClient {

    /** Payment success -> permanently deduct reserved quantity. */
    @PostMapping("/api/inventory/confirm")
    void confirm(@RequestBody StockItem item, @RequestHeader("Authorization") String authorization);

    /** Payment failure -> add reserved quantity back to available stock. */
    @PostMapping("/api/inventory/release")
    void release(@RequestBody StockItem item, @RequestHeader("Authorization") String authorization);
}
