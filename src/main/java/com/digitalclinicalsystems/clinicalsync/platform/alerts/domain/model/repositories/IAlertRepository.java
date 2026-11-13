package com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.repositories;

import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.aggregates.Alert;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.AlertSeverity;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.AlertStatus;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.TriggerSource;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.repositories.ICrudService;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;

import java.util.List;

public interface IAlertRepository extends ICrudService<Alert, Long> {
    List<Alert> findAllByFilters(PatientId patientId, AlertStatus status, AlertSeverity severity);
    boolean existsByTriggerSource(TriggerSource triggerSource);
}
