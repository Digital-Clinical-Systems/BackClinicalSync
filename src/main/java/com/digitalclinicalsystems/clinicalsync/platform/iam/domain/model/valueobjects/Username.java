package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.valueobjects;

/**
 * Handle a professional signs in with, for example {@code enf-001}. Normalised to
 * lower case so the same person cannot be registered twice under a different casing.
 */
public record Username(String username) {

    private static final int MAX_LENGTH = 50;

    public Username {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        username = username.trim().toLowerCase();
        if (username.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Username cannot exceed %d characters".formatted(MAX_LENGTH));
        }
    }
}
