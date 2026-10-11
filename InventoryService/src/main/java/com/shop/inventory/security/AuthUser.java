package com.shop.inventory.security;

public record AuthUser(Long userId, String email, String role) {
}
