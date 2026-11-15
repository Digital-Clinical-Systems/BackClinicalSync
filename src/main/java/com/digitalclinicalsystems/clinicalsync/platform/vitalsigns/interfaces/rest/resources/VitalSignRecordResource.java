package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.interfaces.rest.resources;

import java.time.Instant;

public record VitalSignRecordResource(
        Long id,
        Long patientId,
        Long recordedBy,
        Integer systolic,
        Integer diastolic,
        Integer heartRate,
        Integer oxygenSaturation,
        Double temperature,
        Integer meanArterialPressure,
        String riskLevel,
        Instant measuredAt,
        Long correctsRecordId
) {
}
