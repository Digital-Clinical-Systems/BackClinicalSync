package com.digitalclinicalsystems.clinicalsync.platform.audit.domain.services;

import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.aggregates.AuditLog;
import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.commands.CreateAuditLogCommand;

import java.util.Optional;

public interface IAuditLogCommandService {
    Optional<AuditLog> handle(CreateAuditLogCommand command);
}
