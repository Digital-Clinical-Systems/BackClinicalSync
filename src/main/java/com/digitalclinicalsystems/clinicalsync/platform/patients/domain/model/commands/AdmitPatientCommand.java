package com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.commands;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;

/**
 * @param admittedBy actor of the admission, {@code null} when the system seeds it
 */
public record AdmitPatientCommand(
        String medicalRecordNumber,
        String fullName,
        String admissionDiagnosis,
        Integer age,
        String unit,
        String bed,
        UserId admittedBy
) {
}
