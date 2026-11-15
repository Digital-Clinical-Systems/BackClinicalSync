package com.digitalclinicalsystems.clinicalsync.platform.audit.interfaces.rest.resources;

import tools.jackson.databind.JsonNode;

import java.time.Instant;

public record AuditLogResource(
        Long id,
        String actionType,
        Instant occurredAt,
        Long patientId,
        String affectedResource,
        String actorId,
        JsonNode metadata
) {
}
