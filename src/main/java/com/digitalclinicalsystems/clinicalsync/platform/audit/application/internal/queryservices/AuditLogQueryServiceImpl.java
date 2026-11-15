package com.digitalclinicalsystems.clinicalsync.platform.audit.application.internal.queryservices;

import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.aggregates.AuditLog;
import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.queries.GetAuditLogByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.queries.GetAuditLogsQuery;
import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.repositories.IAuditLogRepository;
import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.valueobjects.ActionType;
import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.services.IAuditLogQueryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuditLogQueryServiceImpl implements IAuditLogQueryService {

    private final IAuditLogRepository auditLogRepository;

    public AuditLogQueryServiceImpl(IAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public List<AuditLog> handle(GetAuditLogsQuery query) {
        var actionType = query.actionType() == null || query.actionType().isBlank() ? null : new ActionType(query.actionType());
        return auditLogRepository.findAllByFilters(query.patientId(), actionType, query.from(), query.to());
    }

    @Override
    public Optional<AuditLog> handle(GetAuditLogByIdQuery query) {
        return auditLogRepository.findById(query.auditLogId());
    }
}
