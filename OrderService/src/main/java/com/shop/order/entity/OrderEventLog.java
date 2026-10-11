package com.shop.order.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Persisted copy of every event published to the order_event topic. */
@Entity
@Table(name = "order_event_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String eventType;

    @Column(nullable = false)
    private Long orderId;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private LocalDateTime createdAt;
}
