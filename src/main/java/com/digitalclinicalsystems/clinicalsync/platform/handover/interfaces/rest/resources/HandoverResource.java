package com.digitalclinicalsystems.clinicalsync.platform.handover.interfaces.rest.resources;

import java.time.Instant;

public record HandoverResource(
        Long id,
        Long patientId,
        Long outgoingNurseId,
        Long incomingNurseId,
        String situation,
        String background,
        String assessment,
        String recommendation,
        String status,
        Instant issuedAt,
        Instant acknowledgedAt
) {
}
