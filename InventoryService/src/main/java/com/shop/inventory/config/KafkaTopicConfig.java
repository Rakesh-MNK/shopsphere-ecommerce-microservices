package com.shop.inventory.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String PRODUCT_EVENT_TOPIC = "product_event";
    public static final String LOW_STOCK_TOPIC = "low_stock_event";

    @Bean
    public NewTopic productEventTopic() {
        return TopicBuilder.name(PRODUCT_EVENT_TOPIC).partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic lowStockTopic() {
        return TopicBuilder.name(LOW_STOCK_TOPIC).partitions(3).replicas(1).build();
    }
}
