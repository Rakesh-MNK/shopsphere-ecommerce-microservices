package com.shop.payment.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.payment.dto.StockItem;
import com.shop.payment.exception.BusinessException;
import com.shop.payment.exception.ResourceNotFoundException;
import com.shop.payment.exception.ServiceUnavailableException;
import feign.FeignException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/** Circuit-breaker fallback: business errors from inventory are passed on, outages become 503. */
@Component
public class InventoryClientFallbackFactory implements FallbackFactory<InventoryClient> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public InventoryClient create(Throwable cause) {
        return new InventoryClient() {
            @Override
            public void confirm(StockItem item, String authorization) {
                throw map(cause);
            }

            @Override
            public void release(StockItem item, String authorization) {
                throw map(cause);
            }
        };
    }

    private RuntimeException map(Throwable cause) {
        if (cause instanceof FeignException fe) {
            if (fe.status() == 404) {
                return new ResourceNotFoundException(message(fe));
            }
            if (fe.status() >= 400 && fe.status() < 500) {
                return new BusinessException(message(fe));
            }
        }
        return new ServiceUnavailableException("inventory-service is currently unavailable, please try again later");
    }

    private String message(FeignException fe) {
        try {
            JsonNode node = MAPPER.readTree(fe.contentUTF8());
            if (node.hasNonNull("message")) {
                return node.get("message").asText();
            }
        } catch (Exception ignored) {
            // fall through
        }
        return "inventory-service rejected the request (HTTP " + fe.status() + ")";
    }
}
