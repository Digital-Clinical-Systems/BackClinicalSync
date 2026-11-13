package com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.queries;

/**
 * @param active {@code null} returns every patient; otherwise filters by admission status
 */
public record GetAllPatientsQuery(Boolean active) {
}
