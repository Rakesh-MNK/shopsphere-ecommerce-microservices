package com.shop.payment.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.payment.config.KafkaTopicConfig;
import com.shop.payment.dto.PaymentEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(PaymentEvent event) {
        try {
            kafkaTemplate.send(KafkaTopicConfig.PAYMENT_EVENT_TOPIC, String.valueOf(event.orderId()),
                    objectMapper.writeValueAsString(event));
            log.info("Published {} for order {}", event.eventType(), event.orderId());
        } catch (JsonProcessingException e) {
            log.error("Could not serialize payment event", e);
        }
    }
}
