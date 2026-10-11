package com.shop.auth.dto;

import com.shop.auth.entity.User;

public record UserResponse(Long id, String name, String email, String role) {

    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole().name());
    }
}
