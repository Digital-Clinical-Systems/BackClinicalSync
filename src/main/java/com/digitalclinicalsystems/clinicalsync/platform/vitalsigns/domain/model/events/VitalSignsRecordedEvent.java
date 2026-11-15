package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.events;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

public record VitalSignsRecordedEvent(
        Long recordId,
        Long patientId,
        Long recordedBy,
        String riskLevel,
        Long correctsRecordId,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String actorId() {
        return DomainEvent.actorOf(recordedBy);
    }

    @Override
    public String affectedResource() {
        return "vital-sign:" + recordId;
    }
}
