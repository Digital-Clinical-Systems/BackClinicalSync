package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.commands;

/** Opens a session. Returns a bearer token, never the stored hash. */
public record SignInCommand(String username, String password) {
}
