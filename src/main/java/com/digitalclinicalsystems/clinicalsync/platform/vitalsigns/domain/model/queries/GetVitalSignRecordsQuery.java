package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.queries;

import java.time.Instant;

/**
 * Every filter is optional; results are returned in chronological order (US-27).
 */
public record GetVitalSignRecordsQuery(Long patientId, Instant from, Instant to) {

    public GetVitalSignRecordsQuery {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("'from' must not be after 'to'");
        }
    }
}
