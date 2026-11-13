package com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.services;

import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.aggregates.Alert;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.queries.GetAlertByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.queries.GetAlertsQuery;

import java.util.List;
import java.util.Optional;

public interface IAlertQueryService {
    List<Alert> handle(GetAlertsQuery query);
    Optional<Alert> handle(GetAlertByIdQuery query);
}
