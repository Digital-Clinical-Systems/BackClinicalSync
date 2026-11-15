package com.digitalclinicalsystems.clinicalsync.platform.audit.interfaces.rest.transform;

import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.aggregates.AuditLog;
import com.digitalclinicalsystems.clinicalsync.platform.audit.interfaces.rest.resources.AuditLogResource;
import tools.jackson.databind.json.JsonMapper;

public class AuditLogResourceFromEntityAssembler {
    public static AuditLogResource toResourceFromEntity(AuditLog auditLog, JsonMapper jsonMapper) {
        return new AuditLogResource(
                auditLog.getId(),
                auditLog.getActionType().actionType(),
                auditLog.getOccurredAt(),
                auditLog.getPatientId(),
                auditLog.getAffectedResource(),
                auditLog.getActorId(),
                jsonMapper.readTree(auditLog.getMetadata()));
    }
}
