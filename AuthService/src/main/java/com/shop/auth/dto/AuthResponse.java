package com.shop.auth.dto;

public record AuthResponse(String accessToken, String tokenType, long expiresInSeconds, UserResponse user) {
}
