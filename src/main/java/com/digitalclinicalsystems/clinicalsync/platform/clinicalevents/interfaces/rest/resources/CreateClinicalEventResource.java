package com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateClinicalEventResource(
        @NotNull @Positive Long patientId,
        @NotBlank String type,
        @NotBlank String severity,
        @NotBlank @Size(max = 2000) String description,
        String medication,
        String dose,
        @Positive Long relatedOrderId
) {
}
