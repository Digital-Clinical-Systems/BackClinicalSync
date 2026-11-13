package com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.interfaces.rest;

import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.application.internal.outboundservices.acl.ExternalIamService;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.queries.GetClinicalEventByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.queries.GetClinicalEventsQuery;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.valueobjects.ClinicalEventType;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.services.IClinicalEventCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.services.IClinicalEventQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.interfaces.rest.resources.ClinicalEventResource;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.interfaces.rest.resources.CreateClinicalEventResource;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.interfaces.rest.transform.ClinicalEventResourceFromEntityAssembler;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.interfaces.rest.transform.RecordClinicalEventCommandFromResourceAssembler;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/clinical-events", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Clinical Events", description = "Medication administrations and relevant clinical events (TS-03, BC-08)")
public class ClinicalEventsController {

    private final IClinicalEventCommandService clinicalEventCommandService;
    private final IClinicalEventQueryService clinicalEventQueryService;
    private final ExternalIamService externalIamService;

    public ClinicalEventsController(IClinicalEventCommandService clinicalEventCommandService,
                                    IClinicalEventQueryService clinicalEventQueryService,
                                    ExternalIamService externalIamService) {
        this.clinicalEventCommandService = clinicalEventCommandService;
        this.clinicalEventQueryService = clinicalEventQueryService;
        this.externalIamService = externalIamService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('NURSE', 'PHYSICIAN')")
    @Operation(summary = "Record clinical event",
            description = "type: MEDICATION_ADMINISTRATION (requires medication and dose) or CLINICAL_OBSERVATION. "
                    + "severity: ROUTINE, NOTABLE or CRITICAL (raises an alert).")
    public ResponseEntity<ClinicalEventResource> recordClinicalEvent(@Valid @RequestBody CreateClinicalEventResource resource) {
        var command = RecordClinicalEventCommandFromResourceAssembler.toCommandFromResource(
                resource, externalIamService.fetchAuthenticatedUserId());
        var clinicalEvent = clinicalEventCommandService.handle(command)
                .orElseThrow(() -> new IllegalStateException("Clinical event could not be recorded"));
        return ResponseEntity.status(HttpStatus.CREATED).body(ClinicalEventResourceFromEntityAssembler.toResourceFromEntity(clinicalEvent));
    }

    @GetMapping
    @Operation(summary = "List clinical events", description = "Chronological order. Optional filters: patientId, type, from, to.")
    public ResponseEntity<List<ClinicalEventResource>> getClinicalEvents(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) ClinicalEventType type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        var events = clinicalEventQueryService.handle(new GetClinicalEventsQuery(patientId, type, from, to)).stream()
                .map(ClinicalEventResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{clinicalEventId}")
    @Operation(summary = "Get clinical event by id")
    public ResponseEntity<ClinicalEventResource> getClinicalEventById(@PathVariable Long clinicalEventId) {
        var clinicalEvent = clinicalEventQueryService.handle(new GetClinicalEventByIdQuery(clinicalEventId))
                .orElseThrow(() -> new ResourceNotFoundException("Clinical event", clinicalEventId));
        return ResponseEntity.ok(ClinicalEventResourceFromEntityAssembler.toResourceFromEntity(clinicalEvent));
    }
}
