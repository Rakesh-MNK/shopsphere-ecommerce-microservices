package com.shop.auth.config;

import com.shop.auth.entity.Role;
import com.shop.auth.entity.User;
import com.shop.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

/** Seeds a default ADMIN account on first start. */
@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner seedAdmin(UserRepository repository,
                                PasswordEncoder encoder,
                                @Value("${app.admin.email}") String email,
                                @Value("${app.admin.password}") String password) {
        return args -> {
            if (!repository.existsByEmail(email)) {
                repository.save(User.builder()
                        .name("System Admin")
                        .email(email)
                        .password(encoder.encode(password))
                        .role(Role.ADMIN)
                        .createdAt(LocalDateTime.now())
                        .build());
                log.info("Default admin created: {}", email);
            }
        };
    }
}
