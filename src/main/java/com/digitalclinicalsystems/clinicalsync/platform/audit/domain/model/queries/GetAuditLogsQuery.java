package com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.queries;

import java.time.Instant;

/**
 * Every filter is optional; entries are returned in chronological order (US-30).
 */
public record GetAuditLogsQuery(Long patientId, String actionType, Instant from, Instant to) {

    public GetAuditLogsQuery {
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("'from' must not be after 'to'");
        }
    }
}
