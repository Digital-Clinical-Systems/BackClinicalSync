package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.events;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;
import java.util.Set;

/**
 * A professional was registered in the service. Not patient-related, so
 * {@code patientId} is null and BC-06 records it as an identity operation.
 */
public record UserSignedUpEvent(
        Long userId,
        String username,
        Set<String> roles,
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
