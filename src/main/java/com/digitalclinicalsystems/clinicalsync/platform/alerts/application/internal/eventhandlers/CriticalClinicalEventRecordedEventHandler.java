package com.digitalclinicalsystems.clinicalsync.platform.alerts.application.internal.eventhandlers;

import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.commands.RaiseAlertCommand;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.AlertSeverity;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.services.IAlertCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.events.CriticalClinicalEventRecordedEvent;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Second source of alerts: a critical clinical event always raises a CRITICAL alert.
 */
@Service
public class CriticalClinicalEventRecordedEventHandler {

    private final IAlertCommandService alertCommandService;

    public CriticalClinicalEventRecordedEventHandler(IAlertCommandService alertCommandService) {
        this.alertCommandService = alertCommandService;
    }

    @EventListener
    public void on(CriticalClinicalEventRecordedEvent event) {
        alertCommandService.handle(new RaiseAlertCommand(
                new PatientId(event.patientId()), AlertSeverity.CRITICAL, event.triggerSource(), event.reason()));
    }
}
