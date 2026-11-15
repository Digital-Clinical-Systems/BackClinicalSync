package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.interfaces.rest;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.application.internal.outboundservices.acl.ExternalIamService;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.queries.GetVitalSignRecordByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.queries.GetVitalSignRecordsQuery;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.services.IVitalSignRecordCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.services.IVitalSignRecordQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.interfaces.rest.resources.CreateVitalSignRecordResource;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.interfaces.rest.resources.VitalSignRecordResource;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.interfaces.rest.transform.RecordVitalSignsCommandFromResourceAssembler;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.interfaces.rest.transform.VitalSignRecordResourceFromEntityAssembler;
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
@RequestMapping(value = "/api/v1/vital-signs", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Vital Signs", description = "Immutable vital sign records with derived clinical risk (TS-03)")
public class VitalSignsController {

    private final IVitalSignRecordCommandService vitalSignRecordCommandService;
    private final IVitalSignRecordQueryService vitalSignRecordQueryService;
    private final ExternalIamService externalIamService;

    public VitalSignsController(IVitalSignRecordCommandService vitalSignRecordCommandService,
                                IVitalSignRecordQueryService vitalSignRecordQueryService,
                                ExternalIamService externalIamService) {
        this.vitalSignRecordCommandService = vitalSignRecordCommandService;
        this.vitalSignRecordQueryService = vitalSignRecordQueryService;
        this.externalIamService = externalIamService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Record vital signs",
            description = "NURSE only. The risk level is derived by the domain; WARNING or CRITICAL raises an alert. "
                    + "To correct a record, send a new one with correctsRecordId.")
    public ResponseEntity<VitalSignRecordResource> recordVitalSigns(@Valid @RequestBody CreateVitalSignRecordResource resource) {
        var command = RecordVitalSignsCommandFromResourceAssembler.toCommandFromResource(
                resource, externalIamService.fetchAuthenticatedUserId());
        var record = vitalSignRecordCommandService.handle(command)
                .orElseThrow(() -> new IllegalStateException("Vital signs could not be recorded"));
        return ResponseEntity.status(HttpStatus.CREATED).body(VitalSignRecordResourceFromEntityAssembler.toResourceFromEntity(record));
    }

    @GetMapping
    @Operation(summary = "List vital sign records", description = "Chronological order. Optional filters: patientId, from, to (ISO-8601).")
    public ResponseEntity<List<VitalSignRecordResource>> getVitalSignRecords(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        var records = vitalSignRecordQueryService.handle(new GetVitalSignRecordsQuery(patientId, from, to)).stream()
                .map(VitalSignRecordResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{recordId}")
    @Operation(summary = "Get vital sign record by id")
    public ResponseEntity<VitalSignRecordResource> getVitalSignRecordById(@PathVariable Long recordId) {
        var record = vitalSignRecordQueryService.handle(new GetVitalSignRecordByIdQuery(recordId))
                .orElseThrow(() -> new ResourceNotFoundException("Vital sign record", recordId));
        return ResponseEntity.ok(VitalSignRecordResourceFromEntityAssembler.toResourceFromEntity(record));
    }
}
