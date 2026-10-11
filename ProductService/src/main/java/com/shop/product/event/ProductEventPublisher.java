package com.shop.product.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.product.config.KafkaTopicConfig;
import com.shop.product.dto.ProductEvent;
import com.shop.product.entity.ProductEventLog;
import com.shop.product.repository.ProductEventLogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ProductEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(ProductEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final ProductEventLogRepository logRepository;

    /** Stores the event in MySQL and publishes it to the product_event topic. */
    public void publish(ProductEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            logRepository.save(new ProductEventLog(null, event.eventType(), event.productId(), json, LocalDateTime.now()));
            kafkaTemplate.send(KafkaTopicConfig.PRODUCT_EVENT_TOPIC, String.valueOf(event.productId()), json)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Failed to publish {} for product {}", event.eventType(), event.productId(), ex);
                        } else {
                            log.info("Published {} for product {} to partition {}", event.eventType(),
                                    event.productId(), result.getRecordMetadata().partition());
                        }
                    });
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize product event", e);
        }
    }
}
