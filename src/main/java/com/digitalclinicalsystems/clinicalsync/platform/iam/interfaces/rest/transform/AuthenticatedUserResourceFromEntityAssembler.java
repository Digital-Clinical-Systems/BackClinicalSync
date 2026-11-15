package com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.transform;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.entities.Role;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.valueobjects.AuthenticatedUser;
import com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;

public class AuthenticatedUserResourceFromEntityAssembler {
    public static AuthenticatedUserResource toResourceFromEntity(AuthenticatedUser authenticatedUser) {
        var user = authenticatedUser.user();
        var roles = user.getRoles().stream().map(Role::getStringName).sorted().toList();
        return new AuthenticatedUserResource(user.getId(), user.getUsername(), user.getFullName(), roles, authenticatedUser.token());
    }
}
