package com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.events;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

public record PatientDischargedEvent(
        Long patientId,
        Long dischargedBy,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String actorId() {
        return DomainEvent.actorOf(dischargedBy);
    }

    @Override
    public String affectedResource() {
        return "patient:" + patientId;
    }
}
