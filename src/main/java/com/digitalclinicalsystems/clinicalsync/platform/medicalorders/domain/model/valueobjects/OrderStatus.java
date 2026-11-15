package com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.valueobjects;

/**
 * An order is never edited: a replacement leaves the previous one SUPERSEDED.
 */
public enum OrderStatus {
    ACTIVE,
    SUPERSEDED
}
