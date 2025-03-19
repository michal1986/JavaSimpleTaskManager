package com.taskmanager.config;

import com.taskmanager.model.Role;
import com.taskmanager.model.User;
import com.taskmanager.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        // Only initialize users if the admin user doesn't exist
        if (!userRepository.existsByUsername("admin")) {
            log.info("Initializing default users...");
            initializeUsers();
            log.info("Default users initialized successfully");
        } else {
            log.info("Users already exist, skipping initialization");
        }
    }

    private void initializeUsers() {
        // Admin user
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        // Regular user
        User johnDoe = new User();
        johnDoe.setUsername("john_doe");
        johnDoe.setEmail("john.doe@example.com");
        johnDoe.setPassword(passwordEncoder.encode("john1234"));
        johnDoe.setRole(Role.USER);
        userRepository.save(johnDoe);

        // Another regular user
        User janeDoe = new User();
        janeDoe.setUsername("jane_doe");
        janeDoe.setEmail("jane.doe@example.com");
        janeDoe.setPassword(passwordEncoder.encode("jane5678"));
        janeDoe.setRole(Role.USER);
        userRepository.save(janeDoe);

        // Manager user
        User manager = new User();
        manager.setUsername("manager");
        manager.setEmail("manager@example.com");
        manager.setPassword(passwordEncoder.encode("manager2024"));
        manager.setRole(Role.MANAGER);
        userRepository.save(manager);
    }
} 