package com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.repositories;

import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.aggregates.AuditLog;
import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.valueobjects.ActionType;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Read and append only: the audit log admits no update and no delete.
 */
public interface IAuditLogRepository {
    AuditLog append(AuditLog auditLog);
    Optional<AuditLog> findById(Long id);
    List<AuditLog> findAllByFilters(Long patientId, ActionType actionType, Instant from, Instant to);
}
