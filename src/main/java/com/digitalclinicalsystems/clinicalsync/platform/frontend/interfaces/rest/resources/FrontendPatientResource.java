package com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.resources;

import java.time.Instant;

/**
 * Patient as the frontend reads it from {@code /patients}.
 */
public record FrontendPatientResource(
        String id,
        String medicalRecordNumber,
        String fullName,
        String admissionDiagnosis,
        Integer age,
        String unit,
        String bed,
        Instant admittedAt,
        Boolean active
) {
}
