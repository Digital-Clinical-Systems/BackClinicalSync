package com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.tokens.jwt.services;

import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.tokens.jwt.IBearerTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Slf4j
@Service
public class TokenServiceImpl implements IBearerTokenService {

    private static final String AUTHORIZATION_PARAMETER_NAME = "Authorization";
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    @Value("${authorization.jwt.secret}")
    private String secret;

    @Value("${authorization.jwt.expiration.days}")
    private int expirationDays;

    @Override
    public String generateToken(String username) {
        var issuedAt = Instant.now();
        var expiration = issuedAt.plus(expirationDays, ChronoUnit.DAYS);
        return Jwts.builder()
                .subject(username)
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .signWith(getSigningKey())
                .compact();
    }

    @Override
    public String getUsernameFromToken(String token) {
        return extractClaims(token).getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.debug("Rejected bearer token: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getBearerTokenFrom(HttpServletRequest request) {
        var header = request.getHeader(AUTHORIZATION_PARAMETER_NAME);
        if (header == null || !header.startsWith(BEARER_TOKEN_PREFIX)) {
            return null;
        }
        return header.substring(BEARER_TOKEN_PREFIX.length());
    }

    private Claims extractClaims(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token).getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
