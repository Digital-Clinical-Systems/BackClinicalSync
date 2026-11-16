package com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.resources;

import java.time.Instant;

/**
 * SBAR handover as the frontend sends and reads it from {@code /handovers}.
 */
public record FrontendHandoverResource(
        String id,
        String patientId,
        String outgoingNurseId,
        String incomingNurseId,
        String situation,
        String background,
        String assessment,
        String recommendation,
        String status,
        Instant issuedAt,
        Instant acknowledgedAt
) {
}
