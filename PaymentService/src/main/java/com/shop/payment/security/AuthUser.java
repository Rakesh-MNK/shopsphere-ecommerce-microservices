package com.shop.payment.security;

public record AuthUser(Long userId, String email, String role) {
}
