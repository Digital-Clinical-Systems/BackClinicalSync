package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.commands;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;

import java.util.Set;

/** Registers a professional. The password arrives in clear text and is hashed before it reaches the aggregate. */
public record SignUpCommand(String username, String fullName, String password, Set<Roles> roles) {
}
