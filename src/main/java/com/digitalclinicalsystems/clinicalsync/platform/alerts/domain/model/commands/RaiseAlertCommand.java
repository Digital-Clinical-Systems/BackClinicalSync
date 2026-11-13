package com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.commands;

import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.AlertSeverity;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;

public record RaiseAlertCommand(PatientId patientId, AlertSeverity severity, String triggerSource, String reason) {
}
