package com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest;

import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.commands.RecordClinicalEventCommand;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.queries.GetClinicalEventByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.queries.GetClinicalEventsQuery;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.valueobjects.ClinicalEventSeverity;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.valueobjects.ClinicalEventType;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.services.IClinicalEventCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.services.IClinicalEventQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.interfaces.rest.transform.ClinicalEventResourceFromEntityAssembler;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.application.internal.ExternalReferenceService;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.domain.model.valueobjects.ReferenceType;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.resources.FrontendClinicalEventResource;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.resources.FrontendVitalSignResource;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.transform.FrontendTranslator;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.commands.RecordVitalSignsCommand;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.queries.GetVitalSignRecordByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.queries.GetVitalSignRecordsQuery;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.services.IVitalSignRecordCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.services.IVitalSignRecordQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.interfaces.rest.transform.VitalSignRecordResourceFromEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Clinical records of the frontend contract: vital signs and clinical events.
 * Posting the same frontend id twice returns the record already created.
 */
@RestController
@RequestMapping(value = FrontendApi.BASE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(name = FrontendApi.ENABLED_PROPERTY, havingValue = "true", matchIfMissing = true)
@Tag(name = "Frontend API", description = FrontendApi.DESCRIPTION)
public class FrontendClinicalRecordsController {

    private final IVitalSignRecordCommandService vitalSignRecordCommandService;
    private final IVitalSignRecordQueryService vitalSignRecordQueryService;
    private final IClinicalEventCommandService clinicalEventCommandService;
    private final IClinicalEventQueryService clinicalEventQueryService;
    private final ExternalReferenceService externalReferenceService;
    private final FrontendTranslator translator;

    public FrontendClinicalRecordsController(IVitalSignRecordCommandService vitalSignRecordCommandService,
                                             IVitalSignRecordQueryService vitalSignRecordQueryService,
                                             IClinicalEventCommandService clinicalEventCommandService,
                                             IClinicalEventQueryService clinicalEventQueryService,
                                             ExternalReferenceService externalReferenceService,
                                             FrontendTranslator translator) {
        this.vitalSignRecordCommandService = vitalSignRecordCommandService;
        this.vitalSignRecordQueryService = vitalSignRecordQueryService;
        this.clinicalEventCommandService = clinicalEventCommandService;
        this.clinicalEventQueryService = clinicalEventQueryService;
        this.externalReferenceService = externalReferenceService;
        this.translator = translator;
    }

    @GetMapping("/vitalSigns")
    @Operation(summary = "Vital sign records")
    public ResponseEntity<List<FrontendVitalSignResource>> getVitalSigns() {
        var context = translator.context();
        var records = vitalSignRecordQueryService.handle(new GetVitalSignRecordsQuery(null, null, null)).stream()
                .map(VitalSignRecordResourceFromEntityAssembler::toResourceFromEntity)
                .map(context::toResource)
                .toList();
        return ResponseEntity.ok(records);
    }

    @PostMapping(value = "/vitalSigns", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Record vital signs",
            description = "The risk level is derived by the backend and WARNING or CRITICAL raises an alert.")
    public ResponseEntity<FrontendVitalSignResource> recordVitalSigns(@RequestBody FrontendVitalSignResource resource) {
        var existing = externalReferenceService.toInternal(ReferenceType.VITAL_SIGN, resource.id())
                .filter(id -> externalReferenceService.isRegistered(ReferenceType.VITAL_SIGN, resource.id()))
                .flatMap(id -> vitalSignRecordQueryService.handle(new GetVitalSignRecordByIdQuery(id)));
        if (existing.isPresent()) {
            return ResponseEntity.ok(translator.context().toResource(
                    VitalSignRecordResourceFromEntityAssembler.toResourceFromEntity(existing.get())));
        }
        var directory = translator.context().directory();
        var correctsRecordId = resource.correctsRecordId() == null ? null
                : internal(ReferenceType.VITAL_SIGN, resource.correctsRecordId(), "Vital sign record");
        var command = new RecordVitalSignsCommand(
                new PatientId(internal(ReferenceType.PATIENT, resource.patientId(), "Patient")),
                new UserId(directory.resolve(resource.recordedBy()).id()),
                resource.systolic(), resource.diastolic(), resource.heartRate(), resource.oxygenSaturation(),
                resource.temperature(), correctsRecordId);
        var record = vitalSignRecordCommandService.handle(command)
                .orElseThrow(() -> new IllegalStateException("Vital signs could not be recorded"));
        externalReferenceService.register(ReferenceType.VITAL_SIGN, resource.id(), record.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(translator.context().toResource(
                VitalSignRecordResourceFromEntityAssembler.toResourceFromEntity(record)));
    }

    @GetMapping("/clinicalEvents")
    @Operation(summary = "Medication administrations and clinical observations")
    public ResponseEntity<List<FrontendClinicalEventResource>> getClinicalEvents() {
        var context = translator.context();
        var events = clinicalEventQueryService.handle(new GetClinicalEventsQuery(null, null, null, null)).stream()
                .map(ClinicalEventResourceFromEntityAssembler::toResourceFromEntity)
                .map(context::toResource)
                .toList();
        return ResponseEntity.ok(events);
    }

    @PostMapping(value = "/clinicalEvents", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Record a clinical event", description = "A CRITICAL event raises an alert.")
    public ResponseEntity<FrontendClinicalEventResource> recordClinicalEvent(@RequestBody FrontendClinicalEventResource resource) {
        var existing = externalReferenceService.toInternal(ReferenceType.CLINICAL_EVENT, resource.id())
                .filter(id -> externalReferenceService.isRegistered(ReferenceType.CLINICAL_EVENT, resource.id()))
                .flatMap(id -> clinicalEventQueryService.handle(new GetClinicalEventByIdQuery(id)));
        if (existing.isPresent()) {
            return ResponseEntity.ok(translator.context().toResource(
                    ClinicalEventResourceFromEntityAssembler.toResourceFromEntity(existing.get())));
        }
        var directory = translator.context().directory();
        var relatedOrderId = resource.relatedOrderId() == null ? null
                : internal(ReferenceType.MEDICAL_ORDER, resource.relatedOrderId(), "Medical order");
        var command = new RecordClinicalEventCommand(
                new PatientId(internal(ReferenceType.PATIENT, resource.patientId(), "Patient")),
                new UserId(directory.resolve(resource.recordedBy()).id()),
                ClinicalEventType.valueOf(resource.type()), ClinicalEventSeverity.valueOf(resource.severity()),
                resource.description(), resource.medication(), resource.dose(), relatedOrderId);
        var event = clinicalEventCommandService.handle(command)
                .orElseThrow(() -> new IllegalStateException("Clinical event could not be recorded"));
        externalReferenceService.register(ReferenceType.CLINICAL_EVENT, resource.id(), event.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(translator.context().toResource(
                ClinicalEventResourceFromEntityAssembler.toResourceFromEntity(event)));
    }

    private Long internal(ReferenceType type, String externalId, String resourceName) {
        return externalReferenceService.toInternal(type, externalId)
                .orElseThrow(() -> new ResourceNotFoundException(resourceName + " " + externalId + " not found"));
    }
}
