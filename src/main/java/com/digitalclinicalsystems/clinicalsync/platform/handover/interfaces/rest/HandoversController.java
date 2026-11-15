package com.digitalclinicalsystems.clinicalsync.platform.handover.interfaces.rest;

import com.digitalclinicalsystems.clinicalsync.platform.handover.application.internal.outboundservices.acl.ExternalIamService;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.commands.AcknowledgeHandoverCommand;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.queries.GetHandoverByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.queries.GetHandoversQuery;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.valueobjects.HandoverStatus;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.services.IHandoverCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.services.IHandoverQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.handover.interfaces.rest.resources.CreateHandoverResource;
import com.digitalclinicalsystems.clinicalsync.platform.handover.interfaces.rest.resources.HandoverResource;
import com.digitalclinicalsystems.clinicalsync.platform.handover.interfaces.rest.transform.HandoverResourceFromEntityAssembler;
import com.digitalclinicalsystems.clinicalsync.platform.handover.interfaces.rest.transform.IssueHandoverCommandFromResourceAssembler;
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
@RequestMapping(value = "/api/v1/handovers", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Handovers", description = "SBAR shift handovers with acknowledgement (TS-04)")
public class HandoversController {

    private final IHandoverCommandService handoverCommandService;
    private final IHandoverQueryService handoverQueryService;
    private final ExternalIamService externalIamService;

    public HandoversController(IHandoverCommandService handoverCommandService,
                               IHandoverQueryService handoverQueryService,
                               ExternalIamService externalIamService) {
        this.handoverCommandService = handoverCommandService;
        this.handoverQueryService = handoverQueryService;
        this.externalIamService = externalIamService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Issue SBAR handover",
            description = "NURSE only. The authenticated nurse is the outgoing nurse; the four SBAR sections are mandatory.")
    public ResponseEntity<HandoverResource> issueHandover(@Valid @RequestBody CreateHandoverResource resource) {
        var command = IssueHandoverCommandFromResourceAssembler.toCommandFromResource(
                resource, externalIamService.fetchAuthenticatedUserId(), externalIamService.fetchAuthenticatedUserRoles());
        var handover = handoverCommandService.handle(command)
                .orElseThrow(() -> new IllegalStateException("Handover could not be issued"));
        return ResponseEntity.status(HttpStatus.CREATED).body(HandoverResourceFromEntityAssembler.toResourceFromEntity(handover));
    }

    @GetMapping
    @Operation(summary = "List handovers", description = "Most recent first. Optional filters: patientId, incomingNurseId (inbox), status.")
    public ResponseEntity<List<HandoverResource>> getHandovers(@RequestParam(required = false) Long patientId,
                                                               @RequestParam(required = false) Long incomingNurseId,
                                                               @RequestParam(required = false) HandoverStatus status) {
        var handovers = handoverQueryService.handle(new GetHandoversQuery(patientId, incomingNurseId, status)).stream()
                .map(HandoverResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(handovers);
    }

    @GetMapping("/{handoverId}")
    @Operation(summary = "Get handover by id")
    public ResponseEntity<HandoverResource> getHandoverById(@PathVariable Long handoverId) {
        var handover = handoverQueryService.handle(new GetHandoverByIdQuery(handoverId))
                .orElseThrow(() -> new ResourceNotFoundException("Handover", handoverId));
        return ResponseEntity.ok(HandoverResourceFromEntityAssembler.toResourceFromEntity(handover));
    }

    @PostMapping("/{handoverId}/acknowledge")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Acknowledge handover", description = "Only the incoming nurse, and only once.")
    public ResponseEntity<HandoverResource> acknowledgeHandover(@PathVariable Long handoverId) {
        var command = new AcknowledgeHandoverCommand(handoverId, externalIamService.fetchAuthenticatedUserId());
        var handover = handoverCommandService.handle(command)
                .orElseThrow(() -> new ResourceNotFoundException("Handover", handoverId));
        return ResponseEntity.ok(HandoverResourceFromEntityAssembler.toResourceFromEntity(handover));
    }
}
