package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.valueobjects;

/**
 * Clinical risk derived from the measured values; never assigned by hand.
 */
public enum RiskLevel {
    NORMAL,
    WARNING,
    CRITICAL
}
