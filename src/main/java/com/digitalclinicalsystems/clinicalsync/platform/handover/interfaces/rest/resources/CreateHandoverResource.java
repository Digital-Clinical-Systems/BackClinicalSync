package com.digitalclinicalsystems.clinicalsync.platform.handover.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * The outgoing nurse is the authenticated user; it is never taken from the body.
 */
public record CreateHandoverResource(
        @NotNull @Positive Long patientId,
        @NotNull @Positive Long incomingNurseId,
        @NotBlank @Size(max = 4000) String situation,
        @NotBlank @Size(max = 4000) String background,
        @NotBlank @Size(max = 4000) String assessment,
        @NotBlank @Size(max = 4000) String recommendation
) {
}
