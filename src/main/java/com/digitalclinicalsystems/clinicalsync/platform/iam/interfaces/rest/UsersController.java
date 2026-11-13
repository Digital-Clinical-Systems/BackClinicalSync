package com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.queries.GetAllUsersQuery;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services.IUserQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.resources.UserResource;
import com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.transform.UserResourceFromEntityAssembler;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Directory of professionals. Read only: users are created through sign-up, and no
 * resource ever exposes a password.
 */
@RestController
@RequestMapping(value = "/api/v1/users", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Users", description = "Directory of professionals of the service")
public class UsersController {

    private final IUserQueryService userQueryService;

    public UsersController(IUserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    @GetMapping
    @Operation(summary = "List users",
            description = "Optionally filtered by role (NURSE, PHYSICIAN, COORDINATOR, AUDITOR).")
    public ResponseEntity<List<UserResource>> getAllUsers(@RequestParam(required = false) String role) {
        var users = userQueryService.handle(new GetAllUsersQuery(role)).stream()
                .map(UserResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by id")
    public ResponseEntity<UserResource> getUserById(@PathVariable Long userId) {
        var user = userQueryService.handle(new GetUserByIdQuery(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        return ResponseEntity.ok(UserResourceFromEntityAssembler.toResourceFromEntity(user));
    }
}
