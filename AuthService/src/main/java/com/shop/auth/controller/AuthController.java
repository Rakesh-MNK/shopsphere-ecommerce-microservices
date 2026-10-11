package com.shop.auth.controller;

import com.shop.auth.dto.AuthResponse;
import com.shop.auth.dto.LoginRequest;
import com.shop.auth.dto.RegisterRequest;
import com.shop.auth.dto.UserResponse;
import com.shop.auth.entity.Role;
import com.shop.auth.security.AuthUser;
import com.shop.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /** Public: registers a CUSTOMER. */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request, Role.CUSTOMER));
    }

    /** ADMIN only: creates another ADMIN account. */
    @PostMapping("/admin/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> registerAdmin(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request, Role.ADMIN));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/me")
    public AuthUser me(@AuthenticationPrincipal AuthUser user) {
        return user;
    }
}
