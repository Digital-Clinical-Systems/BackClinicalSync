package com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SignUpResource(
        @NotBlank @Size(max = 50) String username,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank String fullName,
        @NotEmpty List<@NotBlank String> roles,
        String shift,
        String specialty
) {
}
