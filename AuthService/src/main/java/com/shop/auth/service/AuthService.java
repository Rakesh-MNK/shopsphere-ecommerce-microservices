package com.shop.auth.service;

import com.shop.auth.dto.AuthResponse;
import com.shop.auth.dto.LoginRequest;
import com.shop.auth.dto.RegisterRequest;
import com.shop.auth.dto.UserResponse;
import com.shop.auth.entity.Role;
import com.shop.auth.entity.User;
import com.shop.auth.exception.BusinessException;
import com.shop.auth.exception.UnauthorizedException;
import com.shop.auth.repository.UserRepository;
import com.shop.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserResponse register(RegisterRequest request, Role role) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException("Email already registered: " + email);
        }
        User user = User.builder()
                .name(request.name().trim())
                .email(email)
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();
        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        return new AuthResponse(token, "Bearer", jwtUtil.getExpirationMs() / 1000, UserResponse.from(user));
    }
}
