package com.taskmanager.service;

import com.taskmanager.dto.AuthRequest;
import com.taskmanager.dto.AuthResponse;
import com.taskmanager.dto.RegisterRequest;
import com.taskmanager.exception.UserAlreadyExistsException;
import com.taskmanager.model.User;
import com.taskmanager.repository.UserRepository;
import com.taskmanager.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;
    private AuthRequest authRequest;
    private User user;

    @BeforeEach
    void setUp() {
        // Setup test data
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password123");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
    }

    @Test
    void shouldRegisterNewUser() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn("test-jwt-token");

        // When
        AuthResponse response = authenticationService.register(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals("test-jwt-token", response.getToken());
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(jwtService).generateToken(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUsernameExists() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> {
            authenticationService.register(registerRequest);
        });
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {
        // Given
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> {
            authenticationService.register(registerRequest);
        });
        verify(userRepository).existsByUsername("testuser");
        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldAuthenticateUser() {
        // Given
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("test-jwt-token");
        doNothing().when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        // When
        AuthResponse response = authenticationService.authenticate(authRequest);

        // Then
        assertNotNull(response);
        assertEquals("test-jwt-token", response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername("testuser");
        verify(jwtService).generateToken(user);
    }
} 