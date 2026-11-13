package com.digitalclinicalsystems.clinicalsync.platform.audit.domain.services;

import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.aggregates.AuditLog;
import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.queries.GetAuditLogByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.queries.GetAuditLogsQuery;

import java.util.List;
import java.util.Optional;

public interface IAuditLogQueryService {
    List<AuditLog> handle(GetAuditLogsQuery query);
    Optional<AuditLog> handle(GetAuditLogByIdQuery query);
}
