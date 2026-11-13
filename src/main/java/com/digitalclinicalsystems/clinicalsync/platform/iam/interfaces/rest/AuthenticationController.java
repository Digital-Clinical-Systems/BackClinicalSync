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

/**
 * The only public endpoints of the API (TS-01).
 */
@RestController
@RequestMapping(value = "/api/v1/authentication", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Authentication", description = "Sign in and registration of professionals (TS-01)")
@SecurityRequirements
public class AuthenticationController {

    private final IUserCommandService userCommandService;

    public AuthenticationController(IUserCommandService userCommandService) {
        this.userCommandService = userCommandService;
    }

    @PostMapping(value = "/sign-in", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Sign in",
            description = "Returns the bearer token to send as 'Authorization: Bearer <token>' on every other endpoint.")
    public ResponseEntity<AuthenticatedUserResource> signIn(@Valid @RequestBody SignInResource resource) {
        var command = SignInCommandFromResourceAssembler.toCommandFromResource(resource);
        var authenticatedUser = userCommandService.handle(command)
                .orElseThrow(() -> new IllegalStateException("The session could not be created"));
        return ResponseEntity.ok(
                AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(authenticatedUser));
    }

    @PostMapping(value = "/sign-up", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Sign up",
            description = "Registers a professional with one or more roles. The username must be unique.")
    public ResponseEntity<UserResource> signUp(@Valid @RequestBody SignUpResource resource) {
        var command = SignUpCommandFromResourceAssembler.toCommandFromResource(resource);
        var user = userCommandService.handle(command)
                .orElseThrow(() -> new IllegalStateException("The user could not be created"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UserResourceFromEntityAssembler.toResourceFromEntity(user));
    }
}
