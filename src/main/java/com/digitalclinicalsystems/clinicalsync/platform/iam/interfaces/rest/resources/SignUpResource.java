package com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SignUpResource(
        @NotBlank @Size(max = 50) String username,
        @NotBlank String fullName,
        @NotBlank @Size(min = 8, message = "Password must be at least 8 characters long") String password,
        @NotEmpty(message = "At least one role is required") List<String> roles
) {
}
