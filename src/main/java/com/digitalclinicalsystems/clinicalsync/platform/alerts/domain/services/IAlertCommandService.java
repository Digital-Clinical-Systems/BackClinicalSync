package com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.services;

import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.aggregates.Alert;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.commands.AcknowledgeAlertCommand;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.commands.RaiseAlertCommand;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.commands.ResolveAlertCommand;

import java.util.Optional;

public interface IAlertCommandService {
    Optional<Alert> handle(RaiseAlertCommand command);
    Optional<Alert> handle(AcknowledgeAlertCommand command);
    Optional<Alert> handle(ResolveAlertCommand command);
}
