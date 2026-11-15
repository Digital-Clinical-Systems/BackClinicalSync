package com.digitalclinicalsystems.clinicalsync.platform.audit.interfaces.rest;

import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.queries.GetAuditLogByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.queries.GetAuditLogsQuery;
import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.services.IAuditLogQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.audit.interfaces.rest.resources.AuditLogResource;
import com.digitalclinicalsystems.clinicalsync.platform.audit.interfaces.rest.transform.AuditLogResourceFromEntityAssembler;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.json.JsonMapper;

import java.time.Instant;
import java.util.List;

/**
 * Read-only: the audit log is written by the system and admits no update or delete.
 */
@RestController
@RequestMapping(value = "/api/v1/audit-logs", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Audit Logs", description = "Append-only log of every clinical action (TS-06, US-30, US-32)")
@PreAuthorize("hasAnyRole('COORDINATOR', 'AUDITOR')")
public class AuditLogsController {

    private final IAuditLogQueryService auditLogQueryService;
    private final JsonMapper jsonMapper;

    public AuditLogsController(IAuditLogQueryService auditLogQueryService, JsonMapper jsonMapper) {
        this.auditLogQueryService = auditLogQueryService;
        this.jsonMapper = jsonMapper;
    }

    @GetMapping
    @Operation(summary = "List audit logs",
            description = "COORDINATOR or AUDITOR. Chronological order. Optional filters: patientId, actionType, from, to.")
    public ResponseEntity<List<AuditLogResource>> getAuditLogs(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) String actionType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        var auditLogs = auditLogQueryService.handle(new GetAuditLogsQuery(patientId, actionType, from, to)).stream()
                .map(auditLog -> AuditLogResourceFromEntityAssembler.toResourceFromEntity(auditLog, jsonMapper))
                .toList();
        return ResponseEntity.ok(auditLogs);
    }

    @GetMapping("/{auditLogId}")
    @Operation(summary = "Get audit log by id")
    public ResponseEntity<AuditLogResource> getAuditLogById(@PathVariable Long auditLogId) {
        var auditLog = auditLogQueryService.handle(new GetAuditLogByIdQuery(auditLogId))
                .orElseThrow(() -> new ResourceNotFoundException("Audit log", auditLogId));
        return ResponseEntity.ok(AuditLogResourceFromEntityAssembler.toResourceFromEntity(auditLog, jsonMapper));
    }
}
