package com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.transform;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.valueobjects.AuthenticatedUser;
import com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;

public class AuthenticatedUserResourceFromEntityAssembler {
    public static AuthenticatedUserResource toResourceFromEntity(AuthenticatedUser authenticatedUser) {
        var user = authenticatedUser.user();
        return new AuthenticatedUserResource(
                user.getId(),
                user.getUsername().username(),
                user.getFullName(),
                user.getRoles().stream().map(Roles::name).toList(),
                authenticatedUser.token());
    }
}
