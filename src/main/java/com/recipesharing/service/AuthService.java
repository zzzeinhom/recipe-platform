package com.recipesharing.service;

import com.recipesharing.dto.request.LoginRequest;
import com.recipesharing.dto.request.SignUpRequest;
import com.recipesharing.dto.response.AuthResponse;
import com.recipesharing.entity.User;
import com.recipesharing.entity.UserRole;
import com.recipesharing.exception.BadRequestException;
import com.recipesharing.repository.UserRepository;
import com.recipesharing.security.JwtTokenProvider;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class  AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager     authenticationManager;
    private final JwtTokenProvider jwtProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtTokenProvider jwtProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    @Transactional
    public AuthResponse register(SignUpRequest req) {

        if (userRepository.existsByUsername(req.getUsername())) {
            throw new BadRequestException("Username already taken");
        }

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email already used");
        }

        UserRole role = Boolean.TRUE.equals(req.getRegisterAsChef())
                ? UserRole.CHEF
                : UserRole.USER;

        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .role(role)
                .isActive(true)
                .build();

        userRepository.save(user);

        String token = jwtProvider.generateToken(
                user.getUsername(),
                List.of("ROLE_" + role.name())
        );

        return new AuthResponse(token, user.getUsername(), role.toString());
    }


    public AuthResponse login(LoginRequest req) {

        User user = userRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        Authentication auth;

        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtProvider.generateToken(
                auth.getName(),
                auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
        );

        return new AuthResponse(token, user.getUsername(), user.getRole().toString());
    }
}
