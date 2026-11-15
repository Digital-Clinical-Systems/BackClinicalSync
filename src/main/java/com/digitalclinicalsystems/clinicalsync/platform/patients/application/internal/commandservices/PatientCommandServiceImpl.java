package com.digitalclinicalsystems.clinicalsync.platform.patients.application.internal.commandservices;

import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.aggregates.Patient;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.commands.AdmitPatientCommand;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.commands.DischargePatientCommand;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.repositories.IPatientRepository;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.valueobjects.MedicalRecordNumber;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.services.IPatientCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.BusinessRuleViolationException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PatientCommandServiceImpl implements IPatientCommandService {

    private final IPatientRepository patientRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PatientCommandServiceImpl(IPatientRepository patientRepository, ApplicationEventPublisher eventPublisher) {
        this.patientRepository = patientRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<Patient> handle(AdmitPatientCommand command) {
        var patient = new Patient(command);
        if (patientRepository.existsByMedicalRecordNumber(patient.getMedicalRecordNumber())) {
            throw new BusinessRuleViolationException(
                    "A patient with medical record number %s already exists".formatted(command.medicalRecordNumber()));
        }
        var saved = patientRepository.save(patient);
        saved.registerCreationEvents();
        publishDomainEvents(saved);
        return Optional.of(saved);
    }

    @Override
    public Optional<Patient> handle(DischargePatientCommand command) {
        var patient = patientRepository.findById(command.patientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", command.patientId()));
        patient.discharge(command.dischargedBy());
        var updated = patientRepository.update(patient);
        publishDomainEvents(patient);
        return Optional.of(updated);
    }

    private void publishDomainEvents(Patient patient) {
        patient.domainEvents().forEach(eventPublisher::publishEvent);
        patient.clearDomainEvents();
    }
}
