package com.shop.notification.repository;

import com.shop.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByRecipientRoleOrderByCreatedAtDesc(String role);

    List<Notification> findAllByOrderByCreatedAtDesc();
}
