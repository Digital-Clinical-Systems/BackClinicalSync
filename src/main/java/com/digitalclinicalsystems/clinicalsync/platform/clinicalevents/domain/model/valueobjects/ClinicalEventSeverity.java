package com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.valueobjects;

/**
 * Severity declared by whoever records the event. A critical event raises an alert in BC-04.
 */
public enum ClinicalEventSeverity {
    ROUTINE,
    NOTABLE,
    CRITICAL
}
