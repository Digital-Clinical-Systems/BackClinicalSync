package com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.aggregates;

import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.commands.AdmitPatientCommand;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.events.PatientAdmittedEvent;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.events.PatientDischargedEvent;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.valueobjects.BedLocation;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.valueobjects.MedicalRecordNumber;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.AbstractDomainAggregateRoot;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.BusinessRuleViolationException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;

import java.time.Instant;

/**
 * Aggregate root of BC-02. Master directory: no other context creates patients.
 * Invariants: medical record number and name are mandatory; an admitted patient
 * always has a bed; a patient cannot be discharged twice.
 */
public class Patient extends AbstractDomainAggregateRoot<Patient> {

    private Long id;
    private MedicalRecordNumber medicalRecordNumber;
    private String fullName;
    private String admissionDiagnosis;
    private Integer age;
    private BedLocation bedLocation;
    private Instant admittedAt;
    private UserId admittedBy;
    private boolean active;
    private Instant dischargedAt;
    private UserId dischargedBy;

    private Patient() {
    }

    public Patient(AdmitPatientCommand command) {
        if (command.fullName() == null || command.fullName().isBlank()) {
            throw new IllegalArgumentException("Patient full name is required");
        }
        if (command.age() != null && (command.age() < 0 || command.age() > 130)) {
            throw new IllegalArgumentException("Patient age must be between 0 and 130");
        }
        this.medicalRecordNumber = new MedicalRecordNumber(command.medicalRecordNumber());
        this.fullName = command.fullName().trim();
        this.admissionDiagnosis = command.admissionDiagnosis();
        this.age = command.age();
        this.bedLocation = new BedLocation(command.unit(), command.bed());
        this.admittedAt = Instant.now();
        this.admittedBy = command.admittedBy();
        this.active = true;
    }

    public static Patient reconstitute(
            Long id,
            MedicalRecordNumber medicalRecordNumber,
            String fullName,
            String admissionDiagnosis,
            Integer age,
            BedLocation bedLocation,
            Instant admittedAt,
            UserId admittedBy,
            boolean active,
            Instant dischargedAt,
            UserId dischargedBy) {
        var patient = new Patient();
        patient.id = id;
        patient.medicalRecordNumber = medicalRecordNumber;
        patient.fullName = fullName;
        patient.admissionDiagnosis = admissionDiagnosis;
        patient.age = age;
        patient.bedLocation = bedLocation;
        patient.admittedAt = admittedAt;
        patient.admittedBy = admittedBy;
        patient.active = active;
        patient.dischargedAt = dischargedAt;
        patient.dischargedBy = dischargedBy;
        return patient;
    }

    /** Records the admission event once the patient has an identity. */
    public void registerCreationEvents() {
        registerDomainEvents(new PatientAdmittedEvent(
                id, medicalRecordNumber.medicalRecordNumber(), bedLocation.toString(),
                admittedBy == null ? null : admittedBy.userId(), admittedAt));
    }

    public void discharge(UserId by) {
        if (!active) {
            throw new BusinessRuleViolationException("The patient has already been discharged");
        }
        this.active = false;
        this.dischargedAt = Instant.now();
        this.dischargedBy = by;
        registerDomainEvents(new PatientDischargedEvent(id, by == null ? null : by.userId(), dischargedAt));
    }

    public Long getId() {
        return id;
    }

    public MedicalRecordNumber getMedicalRecordNumber() {
        return medicalRecordNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getAdmissionDiagnosis() {
        return admissionDiagnosis;
    }

    public Integer getAge() {
        return age;
    }

    public BedLocation getBedLocation() {
        return bedLocation;
    }

    public Instant getAdmittedAt() {
        return admittedAt;
    }

    public UserId getAdmittedBy() {
        return admittedBy;
    }

    public boolean isActive() {
        return active;
    }

    public Instant getDischargedAt() {
        return dischargedAt;
    }

    public UserId getDischargedBy() {
        return dischargedBy;
    }
}
