package com.digitalclinicalsystems.clinicalsync.platform.medicalorders.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * The prescriber is the authenticated physician; it is never taken from the body.
 */
public record CreateMedicalOrderResource(
        @NotNull @Positive Long patientId,
        @NotBlank String medication,
        @NotBlank String dose,
        @NotBlank String route,
        @NotBlank String frequency,
        @Positive Long replacesOrderId
) {
}
