package com.digitalclinicalsystems.clinicalsync.platform.alerts.application.internal.queryservices;

import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.aggregates.Alert;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.queries.GetAlertByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.queries.GetAlertsQuery;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.repositories.IAlertRepository;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.services.IAlertQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlertQueryServiceImpl implements IAlertQueryService {

    private final IAlertRepository alertRepository;

    public AlertQueryServiceImpl(IAlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    @Override
    public List<Alert> handle(GetAlertsQuery query) {
        var patientId = query.patientId() == null ? null : new PatientId(query.patientId());
        return alertRepository.findAllByFilters(patientId, query.status(), query.severity());
    }

    @Override
    public Optional<Alert> handle(GetAlertByIdQuery query) {
        return alertRepository.findById(query.alertId());
    }
}
