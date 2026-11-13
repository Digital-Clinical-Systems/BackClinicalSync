package com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.resources;

import java.util.List;

/** Sign-in response. The password hash is never part of any resource. */
public record AuthenticatedUserResource(
        Long id,
        String username,
        String fullName,
        List<String> roles,
        String token
) {
}
