package com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects;

/**
 * Shared Kernel: clinical roles common to every bounded context.
 * Values match {@code iam/domain/model/role.enum.ts} in the frontend.
 */
public enum Roles {
    NURSE,
    PHYSICIAN,
    COORDINATOR,
    AUDITOR;

    /** Authority name used by Spring Security ({@code hasRole('NURSE')}). */
    public String authority() {
        return "ROLE_" + name();
    }

    public static Roles fromName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Role cannot be null or blank");
        }
        var normalized = name.trim().toUpperCase();
        if (normalized.startsWith("ROLE_")) {
            normalized = normalized.substring(5);
        }
        try {
            return Roles.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown role: " + name, e);
        }
    }
}
