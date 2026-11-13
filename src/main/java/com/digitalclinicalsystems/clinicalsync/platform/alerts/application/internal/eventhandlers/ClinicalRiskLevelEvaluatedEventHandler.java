package com.digitalclinicalsystems.clinicalsync.platform.alerts.application.internal.eventhandlers;

import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.commands.RaiseAlertCommand;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.AlertSeverity;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.services.IAlertCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.events.ClinicalRiskLevelEvaluatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Domain policy: a WARNING or CRITICAL risk evaluation raises an alert.
 * Vital Signs publishes the evaluation; deciding to alert belongs to BC-04.
 */
@Service
public class ClinicalRiskLevelEvaluatedEventHandler {

    private static final String WARNING = "WARNING";
    private static final String CRITICAL = "CRITICAL";

    private final IAlertCommandService alertCommandService;

    public ClinicalRiskLevelEvaluatedEventHandler(IAlertCommandService alertCommandService) {
        this.alertCommandService = alertCommandService;
    }

    @EventListener
    public void on(ClinicalRiskLevelEvaluatedEvent event) {
        if (!WARNING.equals(event.riskLevel()) && !CRITICAL.equals(event.riskLevel())) {
            return;
        }
        var severity = CRITICAL.equals(event.riskLevel()) ? AlertSeverity.CRITICAL : AlertSeverity.WARNING;
        alertCommandService.handle(new RaiseAlertCommand(
                new PatientId(event.patientId()), severity, event.triggerSource(), event.reason()));
    }
}
