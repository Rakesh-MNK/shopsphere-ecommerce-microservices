package com.shop.inventory.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.inventory.dto.ProductEvent;
import com.shop.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ProductEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final InventoryService inventoryService;

    @KafkaListener(topics = "product_event", groupId = "inventory-group")
    public void consume(String message) {
        try {
            ProductEvent event = objectMapper.readValue(message, ProductEvent.class);
            log.info("Received {} for product {}", event.eventType(), event.productId());
            inventoryService.handleProductEvent(event);
        } catch (JsonProcessingException e) {
            log.error("Skipping unreadable product event: {}", message, e);
        }
    }
}
