package com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.resources;

import java.time.Instant;

/**
 * Clinical event as the frontend sends and reads it from {@code /clinicalEvents}.
 */
public record FrontendClinicalEventResource(
        String id,
        String patientId,
        String recordedBy,
        String type,
        String severity,
        String description,
        String medication,
        String dose,
        String relatedOrderId,
        Instant occurredAt
) {
}
