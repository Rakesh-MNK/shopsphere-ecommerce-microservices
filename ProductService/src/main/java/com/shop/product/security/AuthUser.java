package com.shop.product.security;

public record AuthUser(Long userId, String email, String role) {
}
