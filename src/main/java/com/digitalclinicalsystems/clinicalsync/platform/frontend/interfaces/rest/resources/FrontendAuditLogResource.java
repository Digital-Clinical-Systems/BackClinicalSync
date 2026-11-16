package com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.resources;

import tools.jackson.databind.JsonNode;

import java.time.Instant;

/**
 * Audit entry as the frontend reads it from {@code /auditLogs}.
 */
public record FrontendAuditLogResource(
        String id,
        String actionType,
        Instant occurredAt,
        String affectedResource,
        String actorId,
        JsonNode metadata
) {
}
