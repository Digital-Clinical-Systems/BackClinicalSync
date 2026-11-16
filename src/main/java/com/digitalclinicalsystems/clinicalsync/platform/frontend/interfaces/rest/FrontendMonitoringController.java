package com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest;

import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.commands.AcknowledgeAlertCommand;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.commands.ResolveAlertCommand;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.queries.GetAlertByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.queries.GetAlertsQuery;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.AlertStatus;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.services.IAlertCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.services.IAlertQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.interfaces.rest.transform.AlertResourceFromEntityAssembler;
import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.queries.GetAuditLogsQuery;
import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.services.IAuditLogQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.audit.interfaces.rest.transform.AuditLogResourceFromEntityAssembler;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.application.internal.ExternalReferenceService;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.domain.model.valueobjects.ReferenceType;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.resources.FrontendAlertChangeResource;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.resources.FrontendAlertResource;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.resources.FrontendAuditLogResource;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.transform.FrontendTranslator;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.util.Comparator;
import java.util.List;

/**
 * Alerts and audit log of the frontend contract.
 * <p>
 * Both are produced by the backend from domain events: an alert when vital signs
 * or a clinical event are critical, an audit entry for every event. The frontend
 * only changes the state of an existing alert.
 */
@RestController
@RequestMapping(value = FrontendApi.BASE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(name = FrontendApi.ENABLED_PROPERTY, havingValue = "true", matchIfMissing = true)
@Tag(name = "Frontend API", description = FrontendApi.DESCRIPTION)
public class FrontendMonitoringController {

    private final IAlertCommandService alertCommandService;
    private final IAlertQueryService alertQueryService;
    private final IAuditLogQueryService auditLogQueryService;
    private final ExternalReferenceService externalReferenceService;
    private final FrontendTranslator translator;
    private final JsonMapper jsonMapper;

    public FrontendMonitoringController(IAlertCommandService alertCommandService,
                                        IAlertQueryService alertQueryService,
                                        IAuditLogQueryService auditLogQueryService,
                                        ExternalReferenceService externalReferenceService,
                                        FrontendTranslator translator,
                                        JsonMapper jsonMapper) {
        this.alertCommandService = alertCommandService;
        this.alertQueryService = alertQueryService;
        this.auditLogQueryService = auditLogQueryService;
        this.externalReferenceService = externalReferenceService;
        this.translator = translator;
        this.jsonMapper = jsonMapper;
    }

    @GetMapping("/alerts")
    @Operation(summary = "Alerts raised by the backend")
    public ResponseEntity<List<FrontendAlertResource>> getAlerts() {
        var context = translator.context();
        var alerts = alertQueryService.handle(new GetAlertsQuery(null, null, null)).stream()
                .map(AlertResourceFromEntityAssembler::toResourceFromEntity)
                .map(context::toResource)
                .toList();
        return ResponseEntity.ok(alerts);
    }

    @PatchMapping(value = "/alerts/{alertId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Acknowledge or resolve an alert",
            description = "ACKNOWLEDGED: acknowledgedBy attends it. RESOLVED: it is resolved, acknowledging it first "
                    + "if needed. An alert the frontend anticipated in memory does not exist here yet (404): the "
                    + "backend raises its own when it records the vital signs.")
    public ResponseEntity<FrontendAlertResource> changeAlert(@PathVariable String alertId,
                                                             @RequestBody FrontendAlertChangeResource resource) {
        var id = externalReferenceService.toInternal(ReferenceType.ALERT, alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert " + alertId + " not found"));
        var alert = alertQueryService.handle(new GetAlertByIdQuery(id))
                .orElseThrow(() -> new ResourceNotFoundException("Alert", id));
        var target = resource.status() == null ? null : AlertStatus.valueOf(resource.status());
        if (target == AlertStatus.ACKNOWLEDGED || target == AlertStatus.RESOLVED) {
            var actor = new UserId(translator.context().directory().resolve(resource.acknowledgedBy()).id());
            if (alert.getStatus() == AlertStatus.OPEN) {
                alert = alertCommandService.handle(new AcknowledgeAlertCommand(id, actor))
                        .orElseThrow(() -> new ResourceNotFoundException("Alert", id));
            }
            if (target == AlertStatus.RESOLVED && alert.getStatus() != AlertStatus.RESOLVED) {
                alert = alertCommandService.handle(new ResolveAlertCommand(id, actor))
                        .orElseThrow(() -> new ResourceNotFoundException("Alert", id));
            }
        }
        return ResponseEntity.ok(translator.context().toResource(AlertResourceFromEntityAssembler.toResourceFromEntity(alert)));
    }

    @GetMapping("/auditLogs")
    @Operation(summary = "Audit log written by the backend for every domain event")
    public ResponseEntity<List<FrontendAuditLogResource>> getAuditLogs() {
        var context = translator.context();
        var auditLogs = auditLogQueryService.handle(new GetAuditLogsQuery(null, null, null, null)).stream()
                .map(auditLog -> AuditLogResourceFromEntityAssembler.toResourceFromEntity(auditLog, jsonMapper))
                .map(context::toResource)
                .sorted(Comparator.comparing(FrontendAuditLogResource::occurredAt).reversed())
                .toList();
        return ResponseEntity.ok(auditLogs);
    }

    /**
     * The audit log is append-only and written by the backend itself, so the
     * entry the frontend anticipates is acknowledged without being stored twice.
     */
    @PostMapping(value = "/auditLogs", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Accepted without changes", description = "The backend audits every domain event itself.")
    public ResponseEntity<JsonNode> postAuditLog(@RequestBody JsonNode resource) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(resource);
    }
}
