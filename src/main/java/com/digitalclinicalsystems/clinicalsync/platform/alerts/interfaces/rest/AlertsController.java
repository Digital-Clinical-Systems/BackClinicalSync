package com.digitalclinicalsystems.clinicalsync.platform.alerts.interfaces.rest;

import com.digitalclinicalsystems.clinicalsync.platform.alerts.application.internal.outboundservices.acl.ExternalIamService;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.commands.AcknowledgeAlertCommand;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.commands.ResolveAlertCommand;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.queries.GetAlertByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.queries.GetAlertsQuery;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.AlertSeverity;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.AlertStatus;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.services.IAlertCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.services.IAlertQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.interfaces.rest.resources.AlertResource;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.interfaces.rest.transform.AlertResourceFromEntityAssembler;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Alerts are raised by the system only, so there is no creation endpoint.
 */
@RestController
@RequestMapping(value = "/api/v1/alerts", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Alerts", description = "Critical events raised from vital signs and clinical events (US-28)")
public class AlertsController {

    private final IAlertCommandService alertCommandService;
    private final IAlertQueryService alertQueryService;
    private final ExternalIamService externalIamService;

    public AlertsController(IAlertCommandService alertCommandService,
                            IAlertQueryService alertQueryService,
                            ExternalIamService externalIamService) {
        this.alertCommandService = alertCommandService;
        this.alertQueryService = alertQueryService;
        this.externalIamService = externalIamService;
    }

    @GetMapping
    @Operation(summary = "List alerts", description = "Most recent first. Optional filters: patientId, status, severity.")
    public ResponseEntity<List<AlertResource>> getAlerts(@RequestParam(required = false) Long patientId,
                                                         @RequestParam(required = false) AlertStatus status,
                                                         @RequestParam(required = false) AlertSeverity severity) {
        var alerts = alertQueryService.handle(new GetAlertsQuery(patientId, status, severity)).stream()
                .map(AlertResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/{alertId}")
    @Operation(summary = "Get alert by id")
    public ResponseEntity<AlertResource> getAlertById(@PathVariable Long alertId) {
        var alert = alertQueryService.handle(new GetAlertByIdQuery(alertId))
                .orElseThrow(() -> new ResourceNotFoundException("Alert", alertId));
        return ResponseEntity.ok(AlertResourceFromEntityAssembler.toResourceFromEntity(alert));
    }

    @PostMapping("/{alertId}/acknowledge")
    @Operation(summary = "Acknowledge alert", description = "OPEN -> ACKNOWLEDGED. Records who attended it and when.")
    public ResponseEntity<AlertResource> acknowledgeAlert(@PathVariable Long alertId) {
        var alert = alertCommandService.handle(new AcknowledgeAlertCommand(alertId, externalIamService.fetchAuthenticatedUserId()))
                .orElseThrow(() -> new ResourceNotFoundException("Alert", alertId));
        return ResponseEntity.ok(AlertResourceFromEntityAssembler.toResourceFromEntity(alert));
    }

    @PostMapping("/{alertId}/resolve")
    @Operation(summary = "Resolve alert", description = "ACKNOWLEDGED -> RESOLVED. An alert cannot be resolved before being acknowledged.")
    public ResponseEntity<AlertResource> resolveAlert(@PathVariable Long alertId) {
        var alert = alertCommandService.handle(new ResolveAlertCommand(alertId, externalIamService.fetchAuthenticatedUserId()))
                .orElseThrow(() -> new ResourceNotFoundException("Alert", alertId));
        return ResponseEntity.ok(AlertResourceFromEntityAssembler.toResourceFromEntity(alert));
    }
}
