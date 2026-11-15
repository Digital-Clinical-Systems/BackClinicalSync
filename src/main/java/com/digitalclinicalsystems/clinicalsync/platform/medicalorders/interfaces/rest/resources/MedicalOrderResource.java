package com.digitalclinicalsystems.clinicalsync.platform.medicalorders.interfaces.rest.resources;

import java.time.Instant;

public record MedicalOrderResource(
        Long id,
        Long patientId,
        Long prescribedBy,
        String medication,
        String dose,
        String route,
        String frequency,
        String status,
        Instant prescribedAt,
        Long replacesOrderId,
        Long supersededBy,
        Instant fulfilledAt,
        Long fulfilledBy
) {
}
