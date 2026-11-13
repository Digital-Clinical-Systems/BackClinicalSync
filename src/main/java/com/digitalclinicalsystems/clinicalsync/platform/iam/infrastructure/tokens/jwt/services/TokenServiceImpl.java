package com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.tokens.jwt.services;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.aggregates.User;
import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.List;

/**
 * JWT adapter for the token port (TS-01). The token carries the username as
 * subject plus the user id and roles as claims, so an authenticated request is
 * resolved without reading the database on every call.
 */
@Slf4j
@Service
public class TokenServiceImpl implements BearerTokenService {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USER_ID_CLAIM = "uid";
    private static final String ROLES_CLAIM = "roles";
    private static final int MINIMUM_SECRET_LENGTH = 32;

    private final SecretKey signingKey;
    private final Duration expiration;

    public TokenServiceImpl(@Value("${authorization.jwt.secret}") String secret,
                            @Value("${authorization.jwt.expiration.days}") int expirationDays) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < MINIMUM_SECRET_LENGTH) {
            throw new IllegalStateException(
                    "authorization.jwt.secret must be at least %d bytes long".formatted(MINIMUM_SECRET_LENGTH));
        }
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expiration = Duration.ofDays(expirationDays);
    }

    @Override
    public String generateToken(User user) {
        var issuedAt = new Date();
        var expiresAt = new Date(issuedAt.getTime() + expiration.toMillis());
        return Jwts.builder()
                .subject(user.getUsername().username())
                .claim(USER_ID_CLAIM, user.getId())
                .claim(ROLES_CLAIM, List.copyOf(user.roleNames()))
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(signingKey)
                .compact();
    }

    @Override
    public String getUsernameFromToken(String token) {
        return Jwts.parser().verifyWith(signingKey).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception) {
            log.debug("Rejected token: {}", exception.getMessage());
            return false;
        }
    }

    @Override
    public String getBearerTokenFrom(HttpServletRequest request) {
        var header = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
