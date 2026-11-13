package com.digitalclinicalsystems.clinicalsync.platform.alerts.interfaces.rest.transform;

import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.aggregates.Alert;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.interfaces.rest.resources.AlertResource;

public class AlertResourceFromEntityAssembler {
    public static AlertResource toResourceFromEntity(Alert alert) {
        return new AlertResource(
                alert.getId(),
                alert.getPatientId().patientId(),
                alert.getSeverity().name(),
                alert.getTriggerSource().triggerSource(),
                alert.getReason(),
                alert.getStatus().name(),
                alert.getRaisedAt(),
                alert.getAcknowledgedBy() == null ? null : alert.getAcknowledgedBy().userId(),
                alert.getAcknowledgedAt(),
                alert.getResolvedBy() == null ? null : alert.getResolvedBy().userId(),
                alert.getResolvedAt());
    }
}
