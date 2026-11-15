package com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.transform;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.commands.SignUpCommand;
import com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.resources.SignUpResource;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;

public class SignUpCommandFromResourceAssembler {
    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        var roles = resource.roles().stream().map(Roles::fromName).distinct().toList();
        return new SignUpCommand(
                resource.username(),
                resource.password(),
                resource.fullName(),
                roles,
                resource.shift(),
                resource.specialty());
    }
}
