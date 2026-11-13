package com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.queries.GetAllRolesQuery;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services.IUserQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.resources.RoleResource;
import com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.rest.transform.RoleResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Role vocabulary, so the client does not hard-code it.
 */
@RestController
@RequestMapping(value = "/api/v1/roles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Roles", description = "Clinical roles recognised by the system")
public class RolesController {

    private final IUserQueryService userQueryService;

    public RolesController(IUserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    @GetMapping
    @Operation(summary = "List roles")
    public ResponseEntity<List<RoleResource>> getAllRoles() {
        var roles = userQueryService.handle(new GetAllRolesQuery()).stream()
                .map(RoleResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(roles);
    }
}
