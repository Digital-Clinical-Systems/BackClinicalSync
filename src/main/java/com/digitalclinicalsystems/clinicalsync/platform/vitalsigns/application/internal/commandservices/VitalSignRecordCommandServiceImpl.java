package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.application.internal.commandservices;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.application.internal.outboundservices.acl.ExternalPatientService;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.aggregates.VitalSignRecord;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.commands.RecordVitalSignsCommand;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.repositories.IVitalSignRecordRepository;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.services.IVitalSignRecordCommandService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VitalSignRecordCommandServiceImpl implements IVitalSignRecordCommandService {

    private final IVitalSignRecordRepository vitalSignRecordRepository;
    private final ExternalPatientService externalPatientService;
    private final ApplicationEventPublisher eventPublisher;

    public VitalSignRecordCommandServiceImpl(IVitalSignRecordRepository vitalSignRecordRepository,
                                             ExternalPatientService externalPatientService,
                                             ApplicationEventPublisher eventPublisher) {
        this.vitalSignRecordRepository = vitalSignRecordRepository;
        this.externalPatientService = externalPatientService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<VitalSignRecord> handle(RecordVitalSignsCommand command) {
        var record = new VitalSignRecord(command);
        externalPatientService.ensurePatientIsAdmitted(record.getPatientId());
        if (record.isCorrection()) {
            var corrected = vitalSignRecordRepository.findById(record.getCorrectsRecordId())
                    .orElseThrow(() -> new ResourceNotFoundException("Vital sign record", record.getCorrectsRecordId()));
            if (!corrected.getPatientId().equals(record.getPatientId())) {
                throw new IllegalArgumentException("A correction must refer to a record of the same patient");
            }
        }
        var saved = vitalSignRecordRepository.save(record);
        saved.registerCreationEvents();
        saved.domainEvents().forEach(eventPublisher::publishEvent);
        saved.clearDomainEvents();
        return Optional.of(saved);
    }
}
