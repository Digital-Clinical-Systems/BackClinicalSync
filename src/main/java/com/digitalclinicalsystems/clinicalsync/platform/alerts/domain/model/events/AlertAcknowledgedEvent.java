package com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.events;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

public record AlertAcknowledgedEvent(
        Long alertId,
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
        return "alert:" + alertId;
    }
}
