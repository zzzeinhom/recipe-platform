package com.recipesharing.security;

import com.recipesharing.config.JwtConfig;
import com.recipesharing.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final JwtConfig props;

    public JwtTokenProvider(JwtConfig props) {
        this.props = props;
        this.key = Keys.hmacShaKeyFor(
                props.getSecret().getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateToken(String username, List<String> roles) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(new Date(now))
                .expiration(new Date(now + props.getExpirationMs()))
                .signWith(key)
                .compact();
    }

    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
