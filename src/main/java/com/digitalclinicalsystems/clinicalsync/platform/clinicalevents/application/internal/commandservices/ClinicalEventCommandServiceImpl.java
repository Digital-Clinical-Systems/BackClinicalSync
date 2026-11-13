package com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.application.internal.commandservices;

import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.application.internal.outboundservices.acl.ExternalPatientService;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.aggregates.ClinicalEvent;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.commands.RecordClinicalEventCommand;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.repositories.IClinicalEventRepository;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.services.IClinicalEventCommandService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClinicalEventCommandServiceImpl implements IClinicalEventCommandService {

    private final IClinicalEventRepository clinicalEventRepository;
    private final ExternalPatientService externalPatientService;
    private final ApplicationEventPublisher eventPublisher;

    public ClinicalEventCommandServiceImpl(IClinicalEventRepository clinicalEventRepository,
                                           ExternalPatientService externalPatientService,
                                           ApplicationEventPublisher eventPublisher) {
        this.clinicalEventRepository = clinicalEventRepository;
        this.externalPatientService = externalPatientService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<ClinicalEvent> handle(RecordClinicalEventCommand command) {
        var clinicalEvent = new ClinicalEvent(command);
        externalPatientService.ensurePatientIsAdmitted(clinicalEvent.getPatientId());
        var saved = clinicalEventRepository.save(clinicalEvent);
        saved.registerCreationEvents();
        saved.domainEvents().forEach(eventPublisher::publishEvent);
        saved.clearDomainEvents();
        return Optional.of(saved);
    }
}
