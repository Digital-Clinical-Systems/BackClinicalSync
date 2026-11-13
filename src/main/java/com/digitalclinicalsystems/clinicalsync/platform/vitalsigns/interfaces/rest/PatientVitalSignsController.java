package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.interfaces.rest;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.application.internal.outboundservices.acl.ExternalPatientService;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.queries.GetVitalSignRecordsQuery;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.services.IVitalSignRecordQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.interfaces.rest.resources.VitalSignRecordResource;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.interfaces.rest.transform.VitalSignRecordResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/patients/{patientId}/vital-signs", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Vital Signs")
public class PatientVitalSignsController {

    private final IVitalSignRecordQueryService vitalSignRecordQueryService;
    private final ExternalPatientService externalPatientService;

    public PatientVitalSignsController(IVitalSignRecordQueryService vitalSignRecordQueryService,
                                       ExternalPatientService externalPatientService) {
        this.vitalSignRecordQueryService = vitalSignRecordQueryService;
        this.externalPatientService = externalPatientService;
    }

    @GetMapping
    @Operation(summary = "Patient recent evolution (US-27)", description = "Records of the patient in chronological order, optionally within [from, to].")
    public ResponseEntity<List<VitalSignRecordResource>> getPatientVitalSigns(
            @PathVariable Long patientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        externalPatientService.ensurePatientExists(new PatientId(patientId));
        var records = vitalSignRecordQueryService.handle(new GetVitalSignRecordsQuery(patientId, from, to)).stream()
                .map(VitalSignRecordResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(records);
    }
}
