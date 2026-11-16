package com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.resources;

/**
 * Professional of the directory, identified by username as the frontend expects.
 */
public record FrontendUserResource(
        String id,
        String fullName,
        String role,
        String shift,
        String specialty
) {
}
