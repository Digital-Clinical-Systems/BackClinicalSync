package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.commands;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;

import java.util.List;

public record SignUpCommand(
        String username,
        String password,
        String fullName,
        List<Roles> roles,
        String shift,
        String specialty
) {
}
