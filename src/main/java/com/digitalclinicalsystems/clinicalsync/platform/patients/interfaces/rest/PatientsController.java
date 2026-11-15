package com.digitalclinicalsystems.clinicalsync.platform.patients.interfaces.rest;

import com.digitalclinicalsystems.clinicalsync.platform.patients.application.internal.outboundservices.acl.ExternalIamService;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.commands.DischargePatientCommand;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.queries.GetAllPatientsQuery;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.queries.GetPatientByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.services.IPatientCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.services.IPatientQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.patients.interfaces.rest.resources.CreatePatientResource;
import com.digitalclinicalsystems.clinicalsync.platform.patients.interfaces.rest.resources.PatientResource;
import com.digitalclinicalsystems.clinicalsync.platform.patients.interfaces.rest.transform.AdmitPatientCommandFromResourceAssembler;
import com.digitalclinicalsystems.clinicalsync.platform.patients.interfaces.rest.transform.PatientResourceFromEntityAssembler;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/patients", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Patients", description = "Patient admission directory (TS-02)")
public class PatientsController {

    private final IPatientCommandService patientCommandService;
    private final IPatientQueryService patientQueryService;
    private final ExternalIamService externalIamService;

    public PatientsController(IPatientCommandService patientCommandService,
                              IPatientQueryService patientQueryService,
                              ExternalIamService externalIamService) {
        this.patientCommandService = patientCommandService;
        this.patientQueryService = patientQueryService;
        this.externalIamService = externalIamService;
    }

    @GetMapping
    @Operation(summary = "List patients", description = "Optionally filtered by admission status (active=true for admitted patients).")
    public ResponseEntity<List<PatientResource>> getAllPatients(@RequestParam(required = false) Boolean active) {
        var patients = patientQueryService.handle(new GetAllPatientsQuery(active)).stream()
                .map(PatientResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(patients);
    }

    @GetMapping("/{patientId}")
    @Operation(summary = "Get patient by id")
    public ResponseEntity<PatientResource> getPatientById(@PathVariable Long patientId) {
        var patient = patientQueryService.handle(new GetPatientByIdQuery(patientId))
                .orElseThrow(() -> new ResourceNotFoundException("Patient", patientId));
        return ResponseEntity.ok(PatientResourceFromEntityAssembler.toResourceFromEntity(patient));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Admit patient", description = "Admits a patient to a unit and bed. The medical record number must be unique.")
    public ResponseEntity<PatientResource> admitPatient(@Valid @RequestBody CreatePatientResource resource) {
        var command = AdmitPatientCommandFromResourceAssembler.toCommandFromResource(
                resource, externalIamService.fetchAuthenticatedUserId());
        var patient = patientCommandService.handle(command)
                .orElseThrow(() -> new IllegalStateException("Patient could not be admitted"));
        return ResponseEntity.status(HttpStatus.CREATED).body(PatientResourceFromEntityAssembler.toResourceFromEntity(patient));
    }

    @PostMapping("/{patientId}/discharge")
    @Operation(summary = "Discharge patient", description = "A patient can only be discharged once.")
    public ResponseEntity<PatientResource> dischargePatient(@PathVariable Long patientId) {
        var command = new DischargePatientCommand(patientId, externalIamService.fetchAuthenticatedUserId());
        var patient = patientCommandService.handle(command)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", patientId));
        return ResponseEntity.ok(PatientResourceFromEntityAssembler.toResourceFromEntity(patient));
    }
}
