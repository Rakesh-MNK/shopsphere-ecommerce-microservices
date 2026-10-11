package com.shop.order.client;

import com.shop.order.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "product-service", fallbackFactory = ProductClientFallbackFactory.class)
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ProductDto getProduct(@PathVariable("id") Long id, @RequestHeader("Authorization") String authorization);
}
