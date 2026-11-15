package com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.events;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

public record HandoverAcknowledgedEvent(
        Long handoverId,
        Long patientId,
        Long acknowledgedBy,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String actorId() {
        return DomainEvent.actorOf(acknowledgedBy);
    }

    @Override
    public String affectedResource() {
        return "handover:" + handoverId;
    }
}
