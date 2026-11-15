package com.digitalclinicalsystems.clinicalsync.platform.audit.infrastructure.persistence.jpa.adapters;

import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.aggregates.AuditLog;
import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.repositories.IAuditLogRepository;
import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.valueobjects.ActionType;
import com.digitalclinicalsystems.clinicalsync.platform.audit.infrastructure.persistence.jpa.entities.AuditLogPersistenceEntity;
import com.digitalclinicalsystems.clinicalsync.platform.audit.infrastructure.persistence.jpa.repositories.AuditLogPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class AuditLogRepositoryAdapterImpl implements IAuditLogRepository {

    private final AuditLogPersistenceRepository auditLogPersistenceRepository;

    public AuditLogRepositoryAdapterImpl(AuditLogPersistenceRepository auditLogPersistenceRepository) {
        this.auditLogPersistenceRepository = auditLogPersistenceRepository;
    }

    @Override
    public AuditLog append(AuditLog auditLog) {
        var entity = new AuditLogPersistenceEntity();
        entity.setActionType(auditLog.getActionType());
        entity.setOccurredAt(auditLog.getOccurredAt());
        entity.setPatientId(auditLog.getPatientId());
        entity.setAffectedResource(auditLog.getAffectedResource());
        entity.setActorId(auditLog.getActorId());
        entity.setMetadata(auditLog.getMetadata());
        return toDomain(auditLogPersistenceRepository.save(entity));
    }

    @Override
    public Optional<AuditLog> findById(Long id) {
        return auditLogPersistenceRepository.findById(id).map(AuditLogRepositoryAdapterImpl::toDomain);
    }

    @Override
    public List<AuditLog> findAllByFilters(Long patientId, ActionType actionType, Instant from, Instant to) {
        return auditLogPersistenceRepository.findAllByFilters(patientId, actionType, from, to).stream()
                .map(AuditLogRepositoryAdapterImpl::toDomain)
                .toList();
    }

    private static AuditLog toDomain(AuditLogPersistenceEntity entity) {
        return AuditLog.reconstitute(
                entity.getId(),
                entity.getActionType(),
                entity.getOccurredAt(),
                entity.getPatientId(),
                entity.getAffectedResource(),
                entity.getActorId(),
                entity.getMetadata());
    }
}
