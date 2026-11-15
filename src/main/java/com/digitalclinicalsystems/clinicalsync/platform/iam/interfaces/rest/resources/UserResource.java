package com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.resources;

import java.util.List;

public record UserResource(
        Long id,
        String username,
        String fullName,
        String role,
        List<String> roles,
        String shift,
        String specialty
) {
}
