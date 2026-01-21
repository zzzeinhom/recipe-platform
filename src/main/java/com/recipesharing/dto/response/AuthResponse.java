package com.recipesharing.dto.response;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class AuthResponse {
    private final String accessToken;
    private final String tokenType = "Bearer";
    private final String username;
    private final String role;
}