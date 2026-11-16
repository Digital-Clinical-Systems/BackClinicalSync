package com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.resources;

import java.time.Instant;

/**
 * Medical order as the frontend sends and reads it from {@code /medicalOrders}.
 */
public record FrontendMedicalOrderResource(
        String id,
        String patientId,
        String prescribedBy,
        String medication,
        String dose,
        String route,
        String frequency,
        String status,
        Instant prescribedAt,
        String replacesOrderId,
        String supersededBy,
        Instant fulfilledAt,
        String fulfilledBy
) {
}
