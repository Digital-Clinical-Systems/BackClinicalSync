package com.digitalclinicalsystems.clinicalsync.platform.patients.interfaces.rest.resources;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePatientResource(
        @NotBlank @Size(max = 50) String medicalRecordNumber,
        @NotBlank String fullName,
        String admissionDiagnosis,
        @Min(0) @Max(130) Integer age,
        @NotBlank String unit,
        @NotBlank String bed
) {
}
