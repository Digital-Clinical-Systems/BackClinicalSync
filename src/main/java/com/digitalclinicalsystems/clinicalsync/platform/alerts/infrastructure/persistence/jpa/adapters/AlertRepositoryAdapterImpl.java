package com.digitalclinicalsystems.clinicalsync.platform.alerts.infrastructure.persistence.jpa.adapters;

import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.aggregates.Alert;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.repositories.IAlertRepository;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.AlertSeverity;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.AlertStatus;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.TriggerSource;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.infrastructure.persistence.jpa.entities.AlertPersistenceEntity;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.infrastructure.persistence.jpa.repositories.AlertPersistenceRepository;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AlertRepositoryAdapterImpl implements IAlertRepository {

    private final AlertPersistenceRepository alertPersistenceRepository;

    public AlertRepositoryAdapterImpl(AlertPersistenceRepository alertPersistenceRepository) {
        this.alertPersistenceRepository = alertPersistenceRepository;
    }

    @Override
    public List<Alert> findAllByFilters(PatientId patientId, AlertStatus status, AlertSeverity severity) {
        return alertPersistenceRepository.findAllByFilters(patientId, status, severity).stream()
                .map(AlertRepositoryAdapterImpl::toDomain).toList();
    }

    @Override
    public boolean existsByTriggerSource(TriggerSource triggerSource) {
        return alertPersistenceRepository.existsByTriggerSource(triggerSource);
    }

    @Override
    public List<Alert> findAll() {
        return alertPersistenceRepository.findAll().stream().map(AlertRepositoryAdapterImpl::toDomain).toList();
    }

    @Override
    public Alert save(Alert alert) {
        var entity = new AlertPersistenceEntity();
        entity.setPatientId(alert.getPatientId());
        entity.setSeverity(alert.getSeverity());
        entity.setTriggerSource(alert.getTriggerSource());
        entity.setReason(alert.getReason());
        entity.setRaisedAt(alert.getRaisedAt());
        copyStateToEntity(alert, entity);
        return toDomain(alertPersistenceRepository.save(entity));
    }

    @Override
    public Alert update(Alert alert) {
        var entity = alertPersistenceRepository.findById(alert.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Alert", alert.getId()));
        copyStateToEntity(alert, entity);
        return toDomain(alertPersistenceRepository.save(entity));
    }

    @Override
    public Optional<Alert> findById(Long id) {
        return alertPersistenceRepository.findById(id).map(AlertRepositoryAdapterImpl::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        alertPersistenceRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        alertPersistenceRepository.deleteAll();
    }

    private static void copyStateToEntity(Alert alert, AlertPersistenceEntity entity) {
        entity.setStatus(alert.getStatus());
        entity.setAcknowledgedBy(alert.getAcknowledgedBy());
        entity.setAcknowledgedAt(alert.getAcknowledgedAt());
        entity.setResolvedBy(alert.getResolvedBy());
        entity.setResolvedAt(alert.getResolvedAt());
    }

    private static Alert toDomain(AlertPersistenceEntity entity) {
        return Alert.reconstitute(
                entity.getId(),
                entity.getPatientId(),
                entity.getSeverity(),
                entity.getTriggerSource(),
                entity.getReason(),
                entity.getStatus(),
                entity.getRaisedAt(),
                entity.getAcknowledgedBy(),
                entity.getAcknowledgedAt(),
                entity.getResolvedBy(),
                entity.getResolvedAt());
    }
}
