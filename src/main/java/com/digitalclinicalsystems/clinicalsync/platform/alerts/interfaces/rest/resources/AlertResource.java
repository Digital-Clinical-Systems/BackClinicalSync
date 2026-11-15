package com.digitalclinicalsystems.clinicalsync.platform.alerts.interfaces.rest.resources;

import java.time.Instant;

public record AlertResource(
        Long id,
        Long patientId,
        String severity,
        String triggerSource,
        String reason,
        String status,
        Instant raisedAt,
        Long acknowledgedBy,
        Instant acknowledgedAt,
        Long resolvedBy,
        Instant resolvedAt
) {
}
