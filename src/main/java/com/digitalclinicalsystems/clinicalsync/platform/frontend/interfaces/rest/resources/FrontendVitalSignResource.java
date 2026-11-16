package com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.resources;

import java.time.Instant;

/**
 * Vital sign record as the frontend sends and reads it from {@code /vitalSigns}.
 */
public record FrontendVitalSignResource(
        String id,
        String patientId,
        String recordedBy,
        Integer systolic,
        Integer diastolic,
        Integer heartRate,
        Integer oxygenSaturation,
        Double temperature,
        Integer meanArterialPressure,
        String riskLevel,
        Instant measuredAt,
        String correctsRecordId
) {
}
