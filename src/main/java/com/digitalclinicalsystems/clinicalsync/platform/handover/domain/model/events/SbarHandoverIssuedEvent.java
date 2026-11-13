package com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.events;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

public record SbarHandoverIssuedEvent(
        Long handoverId,
        Long patientId,
        Long outgoingNurseId,
        Long incomingNurseId,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String actorId() {
        return DomainEvent.actorOf(outgoingNurseId);
    }

    @Override
    public String affectedResource() {
        return "handover:" + handoverId;
    }
}
