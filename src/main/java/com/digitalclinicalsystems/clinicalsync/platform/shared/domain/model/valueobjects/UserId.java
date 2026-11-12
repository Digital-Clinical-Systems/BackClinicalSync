package com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects;

/**
 * Shared Kernel: identifier of a ClinicalSync user (the actor of an operation).
 */
public record UserId(Long userId) {

    public UserId {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
    }
}
