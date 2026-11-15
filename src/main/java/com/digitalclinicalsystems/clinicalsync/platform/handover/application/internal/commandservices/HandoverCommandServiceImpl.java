package com.digitalclinicalsystems.clinicalsync.platform.handover.application.internal.commandservices;

import com.digitalclinicalsystems.clinicalsync.platform.handover.application.internal.outboundservices.acl.ExternalIamService;
import com.digitalclinicalsystems.clinicalsync.platform.handover.application.internal.outboundservices.acl.ExternalPatientService;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.aggregates.Handover;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.commands.AcknowledgeHandoverCommand;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.commands.IssueHandoverCommand;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.repositories.IHandoverRepository;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.services.IHandoverCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HandoverCommandServiceImpl implements IHandoverCommandService {

    private final IHandoverRepository handoverRepository;
    private final ExternalPatientService externalPatientService;
    private final ExternalIamService externalIamService;
    private final ApplicationEventPublisher eventPublisher;

    public HandoverCommandServiceImpl(IHandoverRepository handoverRepository,
                                      ExternalPatientService externalPatientService,
                                      ExternalIamService externalIamService,
                                      ApplicationEventPublisher eventPublisher) {
        this.handoverRepository = handoverRepository;
        this.externalPatientService = externalPatientService;
        this.externalIamService = externalIamService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<Handover> handle(IssueHandoverCommand command) {
        var handover = new Handover(command);
        externalPatientService.ensurePatientIsAdmitted(handover.getPatientId());
        externalIamService.ensureUserIsNurse(handover.getIncomingNurseId());
        var saved = handoverRepository.save(handover);
        saved.registerCreationEvents();
        publishDomainEvents(saved);
        return Optional.of(saved);
    }

    @Override
    public Optional<Handover> handle(AcknowledgeHandoverCommand command) {
        var handover = handoverRepository.findById(command.handoverId())
                .orElseThrow(() -> new ResourceNotFoundException("Handover", command.handoverId()));
        handover.acknowledge(command.acknowledgedBy());
        var updated = handoverRepository.update(handover);
        publishDomainEvents(handover);
        return Optional.of(updated);
    }

    private void publishDomainEvents(Handover handover) {
        handover.domainEvents().forEach(eventPublisher::publishEvent);
        handover.clearDomainEvents();
    }
}
