package com.recipesharing.controller;

import com.recipesharing.dto.request.LoginRequest;
import com.recipesharing.dto.request.SignUpRequest;
import com.recipesharing.dto.response.AuthResponse;
import com.recipesharing.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "User authentication endpoints for login and registration")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new user account
     * @param req Sign up request containing username, email, password, and optional full name and chef registration flag
     * @return AuthResponse with JWT token
     */
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignUpRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    /**
     * Authenticate user with credentials
     * @param req Login credentials containing username (or email) and password
     * @return AuthResponse with JWT token and user role
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        System.out.println("Login attempt for user: " + req.getUsername());
        return ResponseEntity.ok(authService.login(req));
    }
}