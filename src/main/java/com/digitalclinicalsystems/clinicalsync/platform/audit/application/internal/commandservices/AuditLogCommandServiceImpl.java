package com.digitalclinicalsystems.clinicalsync.platform.audit.application.internal.commandservices;

import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.aggregates.AuditLog;
import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.commands.CreateAuditLogCommand;
import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.repositories.IAuditLogRepository;
import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.services.IAuditLogCommandService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuditLogCommandServiceImpl implements IAuditLogCommandService {

    private final IAuditLogRepository auditLogRepository;

    public AuditLogCommandServiceImpl(IAuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public Optional<AuditLog> handle(CreateAuditLogCommand command) {
        return Optional.of(auditLogRepository.append(new AuditLog(command)));
    }
}
