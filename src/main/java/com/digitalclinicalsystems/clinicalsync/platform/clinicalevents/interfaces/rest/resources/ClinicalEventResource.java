package com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.interfaces.rest.resources;

import java.time.Instant;

public record ClinicalEventResource(
        Long id,
        Long patientId,
        Long recordedBy,
        String type,
        String severity,
        String description,
        String medication,
        String dose,
        Long relatedOrderId,
        Instant occurredAt
) {
}
