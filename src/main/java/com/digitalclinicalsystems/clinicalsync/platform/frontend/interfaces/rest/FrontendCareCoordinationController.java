package com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest;

import com.digitalclinicalsystems.clinicalsync.platform.frontend.application.internal.ExternalReferenceService;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.domain.model.valueobjects.ReferenceType;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.resources.FrontendHandoverResource;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.resources.FrontendMedicalOrderResource;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.transform.FrontendTranslator;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.aggregates.Handover;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.commands.AcknowledgeHandoverCommand;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.commands.IssueHandoverCommand;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.queries.GetHandoverByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.queries.GetHandoversQuery;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.valueobjects.HandoverStatus;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.services.IHandoverCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.services.IHandoverQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.handover.interfaces.rest.transform.HandoverResourceFromEntityAssembler;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.aggregates.MedicalOrder;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.commands.FulfillMedicalOrderCommand;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.commands.IssueMedicalOrderCommand;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.queries.GetMedicalOrderByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.queries.GetMedicalOrdersQuery;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.valueobjects.OrderStatus;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.services.IMedicalOrderCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.services.IMedicalOrderQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.interfaces.rest.transform.MedicalOrderResourceFromEntityAssembler;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Medical orders and SBAR handovers of the frontend contract.
 * <p>
 * The frontend saves the whole record on every change, always with POST and the
 * same id. The first POST of an id creates the record; later ones are read as the
 * state transition they carry (acknowledged handover, fulfilled order).
 */
@RestController
@RequestMapping(value = FrontendApi.BASE_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
@ConditionalOnProperty(name = FrontendApi.ENABLED_PROPERTY, havingValue = "true", matchIfMissing = true)
@Tag(name = "Frontend API", description = FrontendApi.DESCRIPTION)
public class FrontendCareCoordinationController {

    private final IMedicalOrderCommandService medicalOrderCommandService;
    private final IMedicalOrderQueryService medicalOrderQueryService;
    private final IHandoverCommandService handoverCommandService;
    private final IHandoverQueryService handoverQueryService;
    private final ExternalReferenceService externalReferenceService;
    private final FrontendTranslator translator;

    public FrontendCareCoordinationController(IMedicalOrderCommandService medicalOrderCommandService,
                                              IMedicalOrderQueryService medicalOrderQueryService,
                                              IHandoverCommandService handoverCommandService,
                                              IHandoverQueryService handoverQueryService,
                                              ExternalReferenceService externalReferenceService,
                                              FrontendTranslator translator) {
        this.medicalOrderCommandService = medicalOrderCommandService;
        this.medicalOrderQueryService = medicalOrderQueryService;
        this.handoverCommandService = handoverCommandService;
        this.handoverQueryService = handoverQueryService;
        this.externalReferenceService = externalReferenceService;
        this.translator = translator;
    }

    @GetMapping("/medicalOrders")
    @Operation(summary = "Medical orders")
    public ResponseEntity<List<FrontendMedicalOrderResource>> getMedicalOrders() {
        var context = translator.context();
        var orders = medicalOrderQueryService.handle(new GetMedicalOrdersQuery(null, null, null)).stream()
                .map(MedicalOrderResourceFromEntityAssembler::toResourceFromEntity)
                .map(context::toResource)
                .toList();
        return ResponseEntity.ok(orders);
    }

    @PostMapping(value = "/medicalOrders", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Issue, replace or fulfill a medical order",
            description = "New id: issues the order (replacing replacesOrderId). Known id with fulfilledAt: records "
                    + "the fulfillment by fulfilledBy. A superseded order needs no call: the backend supersedes it "
                    + "when the replacement is issued.")
    public ResponseEntity<FrontendMedicalOrderResource> saveMedicalOrder(@RequestBody FrontendMedicalOrderResource resource) {
        var directory = translator.context().directory();
        if (externalReferenceService.isRegistered(ReferenceType.MEDICAL_ORDER, resource.id())
                || isNumeric(resource.id())) {
            var orderId = internal(ReferenceType.MEDICAL_ORDER, resource.id(), "Medical order");
            var order = medicalOrderQueryService.handle(new GetMedicalOrderByIdQuery(orderId))
                    .orElseThrow(() -> new ResourceNotFoundException("Medical order", orderId));
            if (resource.fulfilledAt() != null && order.getFulfilledAt() == null
                    && order.getStatus() == OrderStatus.ACTIVE) {
                var nurse = directory.resolve(resource.fulfilledBy());
                order = medicalOrderCommandService.handle(
                                new FulfillMedicalOrderCommand(orderId, new UserId(nurse.id()), nurse.roles()))
                        .orElseThrow(() -> new ResourceNotFoundException("Medical order", orderId));
            }
            return ResponseEntity.ok(toResource(order));
        }
        var prescriber = directory.resolve(resource.prescribedBy());
        var replacesOrderId = resource.replacesOrderId() == null ? null
                : internal(ReferenceType.MEDICAL_ORDER, resource.replacesOrderId(), "Medical order");
        var order = medicalOrderCommandService.handle(new IssueMedicalOrderCommand(
                        new PatientId(internal(ReferenceType.PATIENT, resource.patientId(), "Patient")),
                        new UserId(prescriber.id()), prescriber.roles(), resource.medication(), resource.dose(),
                        resource.route(), resource.frequency(), replacesOrderId))
                .orElseThrow(() -> new IllegalStateException("Medical order could not be issued"));
        externalReferenceService.register(ReferenceType.MEDICAL_ORDER, resource.id(), order.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResource(order));
    }

    @GetMapping("/handovers")
    @Operation(summary = "SBAR handovers")
    public ResponseEntity<List<FrontendHandoverResource>> getHandovers() {
        var context = translator.context();
        var handovers = handoverQueryService.handle(new GetHandoversQuery(null, null, null)).stream()
                .map(HandoverResourceFromEntityAssembler::toResourceFromEntity)
                .map(context::toResource)
                .toList();
        return ResponseEntity.ok(handovers);
    }

    @PostMapping(value = "/handovers", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Issue or acknowledge an SBAR handover",
            description = "New id: issues the handover. Known id with status ACKNOWLEDGED: the incoming nurse "
                    + "acknowledges it.")
    public ResponseEntity<FrontendHandoverResource> saveHandover(@RequestBody FrontendHandoverResource resource) {
        var directory = translator.context().directory();
        if (externalReferenceService.isRegistered(ReferenceType.HANDOVER, resource.id()) || isNumeric(resource.id())) {
            var handoverId = internal(ReferenceType.HANDOVER, resource.id(), "Handover");
            var handover = handoverQueryService.handle(new GetHandoverByIdQuery(handoverId))
                    .orElseThrow(() -> new ResourceNotFoundException("Handover", handoverId));
            if ("ACKNOWLEDGED".equals(resource.status()) && handover.getStatus() != HandoverStatus.ACKNOWLEDGED) {
                var incomingNurse = directory.resolve(resource.incomingNurseId());
                handover = handoverCommandService.handle(
                                new AcknowledgeHandoverCommand(handoverId, new UserId(incomingNurse.id())))
                        .orElseThrow(() -> new ResourceNotFoundException("Handover", handoverId));
            }
            return ResponseEntity.ok(toResource(handover));
        }
        var outgoingNurse = directory.resolve(resource.outgoingNurseId());
        var incomingNurse = directory.resolve(resource.incomingNurseId());
        var handover = handoverCommandService.handle(new IssueHandoverCommand(
                        new PatientId(internal(ReferenceType.PATIENT, resource.patientId(), "Patient")),
                        new UserId(outgoingNurse.id()), outgoingNurse.roles(), new UserId(incomingNurse.id()),
                        resource.situation(), resource.background(), resource.assessment(), resource.recommendation()))
                .orElseThrow(() -> new IllegalStateException("Handover could not be issued"));
        externalReferenceService.register(ReferenceType.HANDOVER, resource.id(), handover.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResource(handover));
    }

    private FrontendMedicalOrderResource toResource(MedicalOrder order) {
        return translator.context().toResource(MedicalOrderResourceFromEntityAssembler.toResourceFromEntity(order));
    }

    private FrontendHandoverResource toResource(Handover handover) {
        return translator.context().toResource(HandoverResourceFromEntityAssembler.toResourceFromEntity(handover));
    }

    private Long internal(ReferenceType type, String externalId, String resourceName) {
        return externalReferenceService.toInternal(type, externalId)
                .orElseThrow(() -> new ResourceNotFoundException(resourceName + " " + externalId + " not found"));
    }

    private static boolean isNumeric(String id) {
        return id != null && !id.isBlank() && id.chars().allMatch(Character::isDigit);
    }
}
