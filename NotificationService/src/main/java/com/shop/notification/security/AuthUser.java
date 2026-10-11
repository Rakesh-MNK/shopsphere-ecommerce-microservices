package com.shop.notification.security;

public record AuthUser(Long userId, String email, String role) {
}
