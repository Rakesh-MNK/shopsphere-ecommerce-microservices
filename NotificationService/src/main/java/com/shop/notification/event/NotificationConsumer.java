package com.shop.notification.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.notification.dto.LowStockEvent;
import com.shop.notification.dto.OrderEvent;
import com.shop.notification.dto.PaymentEvent;
import com.shop.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);
    private static final String GROUP = "notification-group";

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @KafkaListener(topics = "order_event", groupId = GROUP)
    public void onOrderEvent(String message) {
        try {
            notificationService.handleOrderEvent(objectMapper.readValue(message, OrderEvent.class));
        } catch (JsonProcessingException e) {
            log.error("Skipping unreadable order event: {}", message, e);
        }
    }

    @KafkaListener(topics = "payment_event", groupId = GROUP)
    public void onPaymentEvent(String message) {
        try {
            notificationService.handlePaymentEvent(objectMapper.readValue(message, PaymentEvent.class));
        } catch (JsonProcessingException e) {
            log.error("Skipping unreadable payment event: {}", message, e);
        }
    }

    @KafkaListener(topics = "low_stock_event", groupId = GROUP)
    public void onLowStockEvent(String message) {
        try {
            notificationService.handleLowStockEvent(objectMapper.readValue(message, LowStockEvent.class));
        } catch (JsonProcessingException e) {
            log.error("Skipping unreadable low-stock event: {}", message, e);
        }
    }
}
