package com.shop.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    public static final String ORDER_EVENT_TOPIC = "order_event";

    @Bean
    public NewTopic orderEventTopic() {
        return TopicBuilder.name(ORDER_EVENT_TOPIC).partitions(3).replicas(1).build();
    }
}
