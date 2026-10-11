package com.shop.inventory.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.inventory.config.KafkaTopicConfig;
import com.shop.inventory.dto.LowStockEvent;
import com.shop.inventory.entity.Inventory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LowStockPublisher {

    private static final Logger log = LoggerFactory.getLogger(LowStockPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(Inventory inventory) {
        LowStockEvent event = new LowStockEvent("LOW_STOCK", inventory.getProductId(), inventory.getProductName(),
                inventory.getAvailableQty(), inventory.getLowStockThreshold(), LocalDateTime.now());
        try {
            kafkaTemplate.send(KafkaTopicConfig.LOW_STOCK_TOPIC, String.valueOf(event.productId()),
                    objectMapper.writeValueAsString(event));
            log.warn("LOW STOCK published for product {} (available={})", event.productId(), event.availableQty());
        } catch (JsonProcessingException e) {
            log.error("Could not serialize low stock event", e);
        }
    }
}
