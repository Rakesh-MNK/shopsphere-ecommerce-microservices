package com.shop.order.client;

import com.shop.order.dto.ProductDto;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class ProductClientFallbackFactory implements FallbackFactory<ProductClient> {

    @Override
    public ProductClient create(Throwable cause) {
        return new ProductClient() {
            @Override
            public ProductDto getProduct(Long id, String authorization) {
                throw FeignErrors.map(cause, "product-service");
            }
        };
    }
}
