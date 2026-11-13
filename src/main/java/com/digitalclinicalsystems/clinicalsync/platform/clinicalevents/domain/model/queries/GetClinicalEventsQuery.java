package com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.queries;

import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.valueobjects.ClinicalEventType;

import java.time.Instant;

public record GetClinicalEventsQuery(Long patientId, ClinicalEventType type, Instant from, Instant to) {

    public GetClinicalEventsQuery {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("'from' must not be after 'to'");
        }
    }
}
