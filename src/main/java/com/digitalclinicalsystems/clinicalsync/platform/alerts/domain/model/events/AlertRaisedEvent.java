package com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.events;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * Raised by the system as a reaction to another context's event, so the actor is the system.
 */
public record AlertRaisedEvent(
        Long alertId,
        Long patientId,
        String severity,
        String triggerSource,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String actorId() {
        return SYSTEM_ACTOR;
    }

    @Override
    public String affectedResource() {
        return "alert:" + alertId;
    }
}
