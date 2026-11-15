package com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.events;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

public record MedicalOrderFulfilledEvent(
        Long orderId,
        Long patientId,
        Long fulfilledBy,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String actorId() {
        return DomainEvent.actorOf(fulfilledBy);
    }

    @Override
    public String affectedResource() {
        return "medical-order:" + orderId;
    }
}
