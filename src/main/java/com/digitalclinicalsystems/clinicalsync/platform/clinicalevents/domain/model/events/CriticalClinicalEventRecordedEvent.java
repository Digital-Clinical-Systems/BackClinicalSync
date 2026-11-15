package com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.events;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * Published Language towards BC-04: second source of alerts. Clinical Events
 * does not know about alerts, it only states that something critical happened.
 */
public record CriticalClinicalEventRecordedEvent(
        Long clinicalEventId,
        Long patientId,
        Long recordedBy,
        String triggerSource,
        String reason,
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
