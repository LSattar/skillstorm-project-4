package com.skillstorm.animalshelter.services;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    private final SecretKey key;
    private final String issuer;
    private final long expirationSeconds;

    public JwtService(
            @Value("${app.jwt.secret:}") String secret,
            @Value("${app.jwt.issuer:animal-shelter-api}") String issuer,
            @Value("${app.jwt.expiration-seconds:86400}") long expirationSeconds) {
        String s = (secret != null && !secret.isBlank()) ? secret : "change-me-in-production-use-openssl-rand-base64-32";
        if (s.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException("app.jwt.secret must be at least 32 bytes (e.g. openssl rand -base64 32)");
        }
        this.key = Keys.hmacShaKeyFor(s.getBytes(StandardCharsets.UTF_8));
        this.issuer = issuer;
        this.expirationSeconds = expirationSeconds;
    }

    public String createToken(UUID userId, String username, List<String> roles) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationSeconds * 1000);
        return Jwts.builder()
                .subject(userId.toString())
                .claim("username", username)
                .claim("roles", roles)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    /**
     * Creates a short-lived token for OAuth "link" state (e.g. 5 min).
     * Payload: sub = userId, claim "link" = true.
     */
    public String createLinkStateToken(UUID userId, long expirationSeconds) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationSeconds * 1000);
        return Jwts.builder()
                .subject(userId.toString())
                .claim("link", true)
                .issuer(issuer)
                .issuedAt(now)
                .expiration(exp)
                .signWith(key)
                .compact();
    }

    public Claims parseAndValidate(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(issuer)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.debug("JWT validation failed: {}", e.getMessage());
            return null;
        }
    }

    public UUID getUserIdFromClaims(Claims claims) {
        String sub = claims.getSubject();
        return sub != null ? UUID.fromString(sub) : null;
    }
}
