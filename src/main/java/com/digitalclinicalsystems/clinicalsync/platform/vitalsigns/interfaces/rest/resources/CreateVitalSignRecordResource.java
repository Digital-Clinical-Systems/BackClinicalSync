package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * The risk level is not part of the request: it is always derived by the domain.
 */
public record CreateVitalSignRecordResource(
        @NotNull @Positive Long patientId,
        @NotNull Integer systolic,
        @NotNull Integer diastolic,
        @NotNull Integer heartRate,
        @NotNull Integer oxygenSaturation,
        @NotNull Double temperature,
        @Positive Long correctsRecordId
) {
}
