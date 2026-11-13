package com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.aggregates;

import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.commands.RecordClinicalEventCommand;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.events.ClinicalEventRecordedEvent;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.events.CriticalClinicalEventRecordedEvent;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.valueobjects.ClinicalEventSeverity;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.valueobjects.ClinicalEventType;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.AbstractDomainAggregateRoot;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;

import java.time.Instant;
import java.util.ArrayList;

/**
 * Aggregate root of BC-08 (Clinical Events).
 * Invariants: references the patient and who recorded it; the description is
 * mandatory; a medication administration requires medication and dose; the
 * annotation is immutable.
 */
public class ClinicalEvent extends AbstractDomainAggregateRoot<ClinicalEvent> {

    private Long id;
    private final PatientId patientId;
    private final UserId recordedBy;
    private final ClinicalEventType type;
    private final ClinicalEventSeverity severity;
    private final String description;
    private final String medication;
    private final String dose;
    private final Long relatedOrderId;
    private Instant occurredAt;

    public ClinicalEvent(RecordClinicalEventCommand command) {
        this(command.patientId(), command.recordedBy(), command.type(), command.severity(), command.description(),
                command.medication(), command.dose(), command.relatedOrderId());
        this.occurredAt = Instant.now();
    }

    private ClinicalEvent(PatientId patientId, UserId recordedBy, ClinicalEventType type, ClinicalEventSeverity severity,
                          String description, String medication, String dose, Long relatedOrderId) {
        if (patientId == null) {
            throw new IllegalArgumentException("A clinical event must reference a patient");
        }
        if (recordedBy == null) {
            throw new IllegalArgumentException("A clinical event must reference who recorded it");
        }
        if (type == null || severity == null) {
            throw new IllegalArgumentException("A clinical event requires a type and a severity");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("A clinical event requires a description");
        }
        if (type == ClinicalEventType.MEDICATION_ADMINISTRATION) {
            var missing = new ArrayList<String>();
            if (medication == null || medication.isBlank()) missing.add("medication");
            if (dose == null || dose.isBlank()) missing.add("dose");
            if (!missing.isEmpty()) {
                throw new IllegalArgumentException("A medication administration requires complete data: " + String.join(", ", missing));
            }
        }
        this.patientId = patientId;
        this.recordedBy = recordedBy;
        this.type = type;
        this.severity = severity;
        this.description = description.trim();
        this.medication = medication == null || medication.isBlank() ? null : medication.trim();
        this.dose = dose == null || dose.isBlank() ? null : dose.trim();
        this.relatedOrderId = relatedOrderId;
    }

    public static ClinicalEvent reconstitute(Long id, PatientId patientId, UserId recordedBy, ClinicalEventType type,
                                             ClinicalEventSeverity severity, String description, String medication,
                                             String dose, Long relatedOrderId, Instant occurredAt) {
        var event = new ClinicalEvent(patientId, recordedBy, type, severity, description, medication, dose, relatedOrderId);
        event.id = id;
        event.occurredAt = occurredAt;
        return event;
    }

    /** Records the annotation events once it has an identity; a critical one also asks for an alert. */
    public void registerCreationEvents() {
        registerDomainEvents(new ClinicalEventRecordedEvent(
                id, patientId.patientId(), recordedBy.userId(), type.name(), severity.name(), occurredAt));
        if (severity == ClinicalEventSeverity.CRITICAL) {
            registerDomainEvents(new CriticalClinicalEventRecordedEvent(
                    id, patientId.patientId(), recordedBy.userId(), "clinical-event:" + id, description, occurredAt));
        }
    }

    public Long getId() {
        return id;
    }

    public PatientId getPatientId() {
        return patientId;
    }

    public UserId getRecordedBy() {
        return recordedBy;
    }

    public ClinicalEventType getType() {
        return type;
    }

    public ClinicalEventSeverity getSeverity() {
        return severity;
    }

    public String getDescription() {
        return description;
    }

    public String getMedication() {
        return medication;
    }

    public String getDose() {
        return dose;
    }

    public Long getRelatedOrderId() {
        return relatedOrderId;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }
}
