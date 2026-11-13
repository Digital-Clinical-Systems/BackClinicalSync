package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.valueobjects;

/**
 * Already hashed password. The aggregate never holds the plain text: hashing is a
 * port implemented in infrastructure, so a clear-text password cannot reach the
 * domain or the database by accident.
 */
public record HashedPassword(String hash) {

    public HashedPassword {
        if (hash == null || hash.isBlank()) {
            throw new IllegalArgumentException("Password hash is required");
        }
    }
}
