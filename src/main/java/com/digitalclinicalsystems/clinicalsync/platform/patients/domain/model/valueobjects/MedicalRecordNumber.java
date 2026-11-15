package com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.valueobjects;

public record MedicalRecordNumber(String medicalRecordNumber) {

    public MedicalRecordNumber {
        if (medicalRecordNumber == null || medicalRecordNumber.isBlank()) {
            throw new IllegalArgumentException("Medical record number is required");
        }
        medicalRecordNumber = medicalRecordNumber.trim();
    }
}
