package com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.transform;

import com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.resources.RoleResource;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;

public class RoleResourceFromEntityAssembler {
    public static RoleResource toResourceFromEntity(Roles role) {
        return new RoleResource(role.name());
    }
}
