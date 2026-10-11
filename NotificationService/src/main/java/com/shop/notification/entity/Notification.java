package com.shop.notification.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** null for ADMIN-audience alerts (e.g. low stock). */
    private Long recipientUserId;

    @Column(nullable = false, length = 20)
    private String recipientRole;

    @Column(nullable = false, length = 40)
    private String type;

    @Column(nullable = false, length = 50)
    private String channel;

    @Column(length = 150)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(length = 60)
    private String referenceId;

    private LocalDateTime createdAt;
}
