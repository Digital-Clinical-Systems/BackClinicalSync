package com.digitalclinicalsystems.clinicalsync.platform.frontend.domain.model.valueobjects;

/**
 * Kind of record whose identifier the frontend generates on its own.
 * The prefix is the one the frontend uses in trigger sources ({@code vital-sign:<id>}).
 */
public enum ReferenceType {
    PATIENT("patient"),
    VITAL_SIGN("vital-sign"),
    CLINICAL_EVENT("clinical-event"),
    MEDICAL_ORDER("medical-order"),
    HANDOVER("handover"),
    ALERT("alert");

    private final String prefix;

    ReferenceType(String prefix) {
        this.prefix = prefix;
    }

    public String prefix() {
        return prefix;
    }
}
