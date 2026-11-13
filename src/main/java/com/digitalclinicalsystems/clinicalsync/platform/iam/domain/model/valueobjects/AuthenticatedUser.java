package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.valueobjects;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.aggregates.User;

/**
 * Result of a successful sign-in: the user that was authenticated and the bearer
 * token issued for the session.
 */
public record AuthenticatedUser(User user, String token) {

    public AuthenticatedUser {
        if (user == null) {
            throw new IllegalArgumentException("Authenticated user is required");
        }
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Token is required");
        }
    }
}
