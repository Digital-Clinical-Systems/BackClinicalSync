package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.events;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * A professional opened a session. BC-06 keeps it so the audit log can answer who
 * was on the system at a given time, which a clinical record review asks for.
 */
public record UserSignedInEvent(
        Long userId,
        String username,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public Long patientId() {
        return null;
    }

    @Override
    public String actorId() {
        return DomainEvent.actorOf(userId);
    }

    @Override
    public String affectedResource() {
        return "user:" + userId;
    }
}
