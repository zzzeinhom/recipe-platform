package com.recipesharing.controller;

import com.recipesharing.dto.request.LoginRequest;
import com.recipesharing.dto.request.SignUpRequest;
import com.recipesharing.dto.response.AuthResponse;
import com.recipesharing.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Authentication", description = "Login & registration")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    public AuthController(AuthService authService) { this.authService = authService; }

    @Operation(summary = "User registration", description = "Registers a new user account with email and password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registration successful, returns JWT token"),
            @ApiResponse(responseCode = "400", description = "Invalid request body or email already exists"),
            @ApiResponse(responseCode = "409", description = "User with this email already exists")
    })
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignUpRequest req) {
        return ResponseEntity.ok(authService.register(req));
    }

    @Operation(summary = "User login", description = "Authenticates a user with email and password, returns JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, returns JWT token"),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        System.out.println("Login attempt for user: " + req.getUsername());
        return ResponseEntity.ok(authService.login(req));
    }
}
