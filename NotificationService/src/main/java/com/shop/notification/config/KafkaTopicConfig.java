package com.shop.notification.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic orderEventTopic() {
        return TopicBuilder.name("order_event").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic paymentEventTopic() {
        return TopicBuilder.name("payment_event").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic lowStockTopic() {
        return TopicBuilder.name("low_stock_event").partitions(3).replicas(1).build();
    }
}
