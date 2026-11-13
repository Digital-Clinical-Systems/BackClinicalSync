package com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.events;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

public record PatientAdmittedEvent(
        Long patientId,
        String medicalRecordNumber,
        String bedLocation,
        Long admittedBy,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String actorId() {
        return DomainEvent.actorOf(admittedBy);
    }

    @Override
    public String affectedResource() {
        return "patient:" + patientId;
    }
}
