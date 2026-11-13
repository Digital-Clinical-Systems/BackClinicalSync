package com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.commands;

import java.time.Instant;

/**
 * Issued by the system when it consumes a domain event, never by a user.
 */
public record CreateAuditLogCommand(
        String eventName,
        Instant occurredAt,
        Long patientId,
        String affectedResource,
        String actorId,
        String metadata
) {
}
