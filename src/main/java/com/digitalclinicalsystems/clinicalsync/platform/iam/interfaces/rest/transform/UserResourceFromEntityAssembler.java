package com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.transform;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.aggregates.User;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.entities.Role;
import com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.resources.UserResource;

public class UserResourceFromEntityAssembler {
    public static UserResource toResourceFromEntity(User user) {
        var roles = user.getRoles().stream().map(Role::getStringName).sorted().toList();
        return new UserResource(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getPrimaryRole().name(),
                roles,
                user.getShift(),
                user.getSpecialty());
    }
}
