package com.digitalclinicalsystems.clinicalsync.platform.patients.interfaces.rest.resources;

import java.time.Instant;

public record PatientResource(
        Long id,
        String medicalRecordNumber,
        String fullName,
        String admissionDiagnosis,
        Integer age,
        String unit,
        String bed,
        Instant admittedAt,
        boolean active,
        Instant dischargedAt
) {
}
