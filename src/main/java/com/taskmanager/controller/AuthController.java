package com.taskmanager.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanager.dto.AuthRequest;
import com.taskmanager.dto.AuthResponse;
import com.taskmanager.dto.RegisterRequest;
import com.taskmanager.service.AuthenticationService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authenticationService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse authenticate(@RequestBody AuthRequest request) {
        return authenticationService.authenticate(request);
    }
} 