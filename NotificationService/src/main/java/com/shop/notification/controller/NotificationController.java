package com.shop.notification.controller;

import com.shop.notification.dto.NotificationResponse;
import com.shop.notification.security.AuthUser;
import com.shop.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<NotificationResponse> my(@AuthenticationPrincipal AuthUser user) {
        return notificationService.getForUser(user.userId());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<NotificationResponse> all() {
        return notificationService.getAll();
    }

    @GetMapping("/admin-alerts")
    @PreAuthorize("hasRole('ADMIN')")
    public List<NotificationResponse> adminAlerts() {
        return notificationService.getAdminAlerts();
    }
}
