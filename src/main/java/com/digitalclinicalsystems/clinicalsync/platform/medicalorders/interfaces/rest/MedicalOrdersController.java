package com.digitalclinicalsystems.clinicalsync.platform.medicalorders.interfaces.rest;

import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.application.internal.outboundservices.acl.ExternalIamService;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.commands.FulfillMedicalOrderCommand;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.queries.GetMedicalOrderByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.queries.GetMedicalOrdersQuery;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.valueobjects.OrderStatus;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.services.IMedicalOrderCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.services.IMedicalOrderQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.interfaces.rest.resources.CreateMedicalOrderResource;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.interfaces.rest.resources.MedicalOrderResource;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.interfaces.rest.transform.IssueMedicalOrderCommandFromResourceAssembler;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.interfaces.rest.transform.MedicalOrderResourceFromEntityAssembler;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/medical-orders", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Medical Orders", description = "Physician orders, replacement and nursing fulfillment (TS-05)")
public class MedicalOrdersController {

    private final IMedicalOrderCommandService medicalOrderCommandService;
    private final IMedicalOrderQueryService medicalOrderQueryService;
    private final ExternalIamService externalIamService;

    public MedicalOrdersController(IMedicalOrderCommandService medicalOrderCommandService,
                                   IMedicalOrderQueryService medicalOrderQueryService,
                                   ExternalIamService externalIamService) {
        this.medicalOrderCommandService = medicalOrderCommandService;
        this.medicalOrderQueryService = medicalOrderQueryService;
        this.externalIamService = externalIamService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('PHYSICIAN')")
    @Operation(summary = "Issue medical order",
            description = "PHYSICIAN only. With replacesOrderId the previous active order becomes SUPERSEDED.")
    public ResponseEntity<MedicalOrderResource> issueMedicalOrder(@Valid @RequestBody CreateMedicalOrderResource resource) {
        var command = IssueMedicalOrderCommandFromResourceAssembler.toCommandFromResource(
                resource, externalIamService.fetchAuthenticatedUserId(), externalIamService.fetchAuthenticatedUserRoles());
        var order = medicalOrderCommandService.handle(command)
                .orElseThrow(() -> new IllegalStateException("Medical order could not be issued"));
        return ResponseEntity.status(HttpStatus.CREATED).body(MedicalOrderResourceFromEntityAssembler.toResourceFromEntity(order));
    }

    @GetMapping
    @Operation(summary = "List medical orders",
            description = "Most recent first. Optional filters: patientId, status (ACTIVE, SUPERSEDED), pending (active without fulfillment).")
    public ResponseEntity<List<MedicalOrderResource>> getMedicalOrders(@RequestParam(required = false) Long patientId,
                                                                       @RequestParam(required = false) OrderStatus status,
                                                                       @RequestParam(required = false) Boolean pending) {
        var orders = medicalOrderQueryService.handle(new GetMedicalOrdersQuery(patientId, status, pending)).stream()
                .map(MedicalOrderResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get medical order by id")
    public ResponseEntity<MedicalOrderResource> getMedicalOrderById(@PathVariable Long orderId) {
        var order = medicalOrderQueryService.handle(new GetMedicalOrderByIdQuery(orderId))
                .orElseThrow(() -> new ResourceNotFoundException("Medical order", orderId));
        return ResponseEntity.ok(MedicalOrderResourceFromEntityAssembler.toResourceFromEntity(order));
    }

    @PostMapping("/{orderId}/fulfill")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Record fulfillment (US-24)", description = "NURSE only, once, on an active order and never by its prescriber.")
    public ResponseEntity<MedicalOrderResource> fulfillMedicalOrder(@PathVariable Long orderId) {
        var command = new FulfillMedicalOrderCommand(
                orderId, externalIamService.fetchAuthenticatedUserId(), externalIamService.fetchAuthenticatedUserRoles());
        var order = medicalOrderCommandService.handle(command)
                .orElseThrow(() -> new ResourceNotFoundException("Medical order", orderId));
        return ResponseEntity.ok(MedicalOrderResourceFromEntityAssembler.toResourceFromEntity(order));
    }
}
