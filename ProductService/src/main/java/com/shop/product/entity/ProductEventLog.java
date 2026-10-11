package com.shop.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** Persisted copy of every event published to the product_event topic. */
@Entity
@Table(name = "product_event_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductEventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String eventType;

    @Column(nullable = false)
    private Long productId;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private LocalDateTime createdAt;
}
