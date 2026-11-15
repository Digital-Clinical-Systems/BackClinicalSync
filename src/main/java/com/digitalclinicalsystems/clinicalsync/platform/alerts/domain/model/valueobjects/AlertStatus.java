package com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects;

/**
 * Lifecycle OPEN -> ACKNOWLEDGED -> RESOLVED. It never goes back.
 */
public enum AlertStatus {
    OPEN,
    ACKNOWLEDGED,
    RESOLVED
}
