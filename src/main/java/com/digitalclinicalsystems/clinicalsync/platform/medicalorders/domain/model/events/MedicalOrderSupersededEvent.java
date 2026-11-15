package com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.events;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

public record MedicalOrderSupersededEvent(
        Long orderId,
        Long patientId,
        Long supersededBy,
        Long prescribedBy,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public String actorId() {
        return DomainEvent.actorOf(prescribedBy);
    }

    @Override
    public String affectedResource() {
        return "medical-order:" + orderId;
    }
}
