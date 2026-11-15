package com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.events;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

public record ClinicalEventRecordedEvent(
        Long clinicalEventId,
        Long patientId,
        Long recordedBy,
        String eventType,
        String severity,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String actorId() {
        return DomainEvent.actorOf(recordedBy);
    }

    @Override
    public String affectedResource() {
        return "clinical-event:" + clinicalEventId;
    }
}
