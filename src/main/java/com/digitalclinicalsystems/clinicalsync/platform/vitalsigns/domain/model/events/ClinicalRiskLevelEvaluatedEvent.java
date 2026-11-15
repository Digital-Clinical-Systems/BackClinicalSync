package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.events;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.events.DomainEvent;

import java.time.Instant;

/**
 * Published Language towards BC-04. Vital Signs does not know about alerts: it
 * only states the evaluated risk; deciding whether an alert is raised belongs to Alerts.
 *
 * @param triggerSource origin reference with the {@code vital-sign:} prefix
 * @param reason        human readable summary of the values out of threshold
 */
public record ClinicalRiskLevelEvaluatedEvent(
        Long recordId,
        Long patientId,
        Long recordedBy,
        String riskLevel,
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
        return "vital-sign:" + recordId;
    }
}
