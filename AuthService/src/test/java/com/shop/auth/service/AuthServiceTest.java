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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthService authService;

    @Test
    void register_duplicateEmail_throwsBusinessException() {
        when(userRepository.existsByEmail("a@b.com")).thenReturn(true);
        assertThrows(BusinessException.class,
                () -> authService.register(new RegisterRequest("A", "a@b.com", "secret1"), Role.CUSTOMER));
    }

    @Test
    void register_success_hashesPasswordAndSetsRole() {
        when(userRepository.existsByEmail("a@b.com")).thenReturn(false);
        when(passwordEncoder.encode("secret1")).thenReturn("hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserResponse response = authService.register(new RegisterRequest("A", "A@b.com", "secret1"), Role.CUSTOMER);

        assertEquals(1L, response.id());
        assertEquals("a@b.com", response.email());
        assertEquals("CUSTOMER", response.role());
    }

    @Test
    void login_wrongPassword_throwsUnauthorized() {
        User user = User.builder().id(1L).email("a@b.com").password("hashed").role(Role.CUSTOMER).name("A").build();
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("bad", "hashed")).thenReturn(false);
        assertThrows(UnauthorizedException.class, () -> authService.login(new LoginRequest("a@b.com", "bad")));
    }

    @Test
    void login_success_returnsToken() {
        User user = User.builder().id(7L).email("a@b.com").password("hashed").role(Role.ADMIN).name("A").build();
        when(userRepository.findByEmail("a@b.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("good", "hashed")).thenReturn(true);
        when(jwtUtil.generateToken(7L, "a@b.com", "ADMIN")).thenReturn("jwt-token");
        when(jwtUtil.getExpirationMs()).thenReturn(3600000L);

        AuthResponse response = authService.login(new LoginRequest("a@b.com", "good"));

        assertEquals("jwt-token", response.accessToken());
        assertEquals("ADMIN", response.user().role());
        assertEquals(3600L, response.expiresInSeconds());
    }
}
