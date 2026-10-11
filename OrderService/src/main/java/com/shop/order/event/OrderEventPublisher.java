package com.shop.order.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.order.config.KafkaTopicConfig;
import com.shop.order.dto.OrderEvent;
import com.shop.order.entity.Order;
import com.shop.order.entity.OrderEventLog;
import com.shop.order.repository.OrderEventLogRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final OrderEventLogRepository logRepository;

    /** Stores the event in MySQL and publishes it to the order_event topic. */
    public void publish(String eventType, Order order) {
        OrderEvent event = new OrderEvent(eventType, order.getId(), order.getUserId(), order.getStatus().name(),
                order.getTotalAmount(), LocalDateTime.now());
        try {
            String json = objectMapper.writeValueAsString(event);
            logRepository.save(new OrderEventLog(null, eventType, order.getId(), json, LocalDateTime.now()));
            kafkaTemplate.send(KafkaTopicConfig.ORDER_EVENT_TOPIC, String.valueOf(order.getId()), json);
            log.info("Published {} for order {}", eventType, order.getId());
        } catch (JsonProcessingException e) {
            log.error("Could not serialize order event", e);
        }
    }
}
