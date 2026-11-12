package com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects;

/**
 * Shared Kernel: identifier of a patient owned by BC-02. Other contexts only
 * keep this reference, never the patient data itself.
 */
public record PatientId(Long patientId) {

    public PatientId {
        if (patientId == null) {
            throw new IllegalArgumentException("patientId cannot be null");
        }
    }
}
