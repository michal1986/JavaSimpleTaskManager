package com.taskmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.dto.AuthRequest;
import com.taskmanager.dto.AuthResponse;
import com.taskmanager.dto.RegisterRequest;
import com.taskmanager.exception.UserAlreadyExistsException;
import com.taskmanager.security.JwtAuthenticationFilter;
import com.taskmanager.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void shouldRegisterUser() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");

        AuthResponse authResponse = new AuthResponse("test-jwt-token");
        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"));

        verify(authenticationService).register(any(RegisterRequest.class));
    }

    @Test
    void shouldReturnConflictWhenUsernameExists() throws Exception {
        // Given
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("existinguser");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");

        when(authenticationService.register(any(RegisterRequest.class)))
                .thenThrow(new UserAlreadyExistsException("Username already exists"));

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isConflict());

        verify(authenticationService).register(any(RegisterRequest.class));
    }

    @Test
    void shouldAuthenticateUser() throws Exception {
        // Given
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("admin");
        authRequest.setPassword("admin123");

        AuthResponse authResponse = new AuthResponse("test-jwt-token");
        when(authenticationService.authenticate(any(AuthRequest.class))).thenReturn(authResponse);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"));

        verify(authenticationService).authenticate(any(AuthRequest.class));
    }

    @Test
    void shouldReturnUnauthorizedForInvalidCredentials() throws Exception {
        // Given
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("admin");
        authRequest.setPassword("wrongpassword");

        when(authenticationService.authenticate(any(AuthRequest.class)))
                .thenThrow(new org.springframework.security.authentication.BadCredentialsException("Bad credentials"));

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());

        verify(authenticationService).authenticate(any(AuthRequest.class));
    }
} 