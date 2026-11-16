package com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.resources;

import java.time.Instant;

/**
 * Alert as the frontend reads it from {@code /alerts}.
 */
public record FrontendAlertResource(
        String id,
        String patientId,
        String severity,
        String triggerSource,
        String reason,
        String status,
        Instant raisedAt,
        String acknowledgedBy
) {
}
