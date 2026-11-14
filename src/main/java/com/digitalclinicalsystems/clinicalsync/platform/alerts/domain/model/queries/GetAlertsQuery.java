package com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.queries;

import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.AlertSeverity;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.AlertStatus;

public record GetAlertsQuery(Long patientId, AlertStatus status, AlertSeverity severity) {
}
