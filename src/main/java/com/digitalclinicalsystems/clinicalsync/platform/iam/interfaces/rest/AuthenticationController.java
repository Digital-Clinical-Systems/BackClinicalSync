package com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services.IUserCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.resources.SignInResource;
import com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.resources.SignUpResource;
import com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.resources.UserResource;
import com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.transform.SignInCommandFromResourceAssembler;
import com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.transform.SignUpCommandFromResourceAssembler;
import com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Sign-in and sign-up endpoints (TS-01)")
@SecurityRequirements
public class AuthenticationController {

    private final IUserCommandService userCommandService;

    public AuthenticationController(IUserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    @PostMapping("/sign-in")
    @Operation(summary = "Sign in", description = "Validates the credentials and returns a bearer token.")
    public ResponseEntity<AuthenticatedUserResource> signIn(@Valid @RequestBody SignInResource resource) {
        var command = SignInCommandFromResourceAssembler.toCommandFromResource(resource);
        return userCommandService.handle(command)
                .map(AuthenticatedUserResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PostMapping("/sign-up")
    @Operation(summary = "Sign up", description = "Registers a user with one or more roles (NURSE, PHYSICIAN, COORDINATOR, AUDITOR).")
    public ResponseEntity<UserResource> signUp(@Valid @RequestBody SignUpResource resource) {
        var command = SignUpCommandFromResourceAssembler.toCommandFromResource(resource);
        return userCommandService.handle(command)
                .map(UserResourceFromEntityAssembler::toResourceFromEntity)
                .map(user -> ResponseEntity.status(HttpStatus.CREATED).body(user))
                .orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
