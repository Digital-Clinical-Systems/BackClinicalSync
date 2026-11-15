package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.valueobjects;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.aggregates.User;

/**
 * Result of a successful sign-in: the user and the bearer token issued for it.
 */
public record AuthenticatedUser(User user, String token) {

    public AuthenticatedUser {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("token cannot be null or blank");
        }
    }
}
