package com.shop.notification.dto;

import com.shop.notification.entity.Notification;

import java.time.LocalDateTime;

public record NotificationResponse(Long id, Long recipientUserId, String recipientRole, String type, String channel,
                                   String subject, String message, String referenceId, LocalDateTime createdAt) {

    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(n.getId(), n.getRecipientUserId(), n.getRecipientRole(), n.getType(),
                n.getChannel(), n.getSubject(), n.getMessage(), n.getReferenceId(), n.getCreatedAt());
    }
}
