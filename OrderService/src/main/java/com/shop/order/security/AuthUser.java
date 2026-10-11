package com.shop.order.security;

public record AuthUser(Long userId, String email, String role) {
}
