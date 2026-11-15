package com.digitalclinicalsystems.clinicalsync.platform.patients.interfaces.rest.transform;

import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.aggregates.Patient;
import com.digitalclinicalsystems.clinicalsync.platform.patients.interfaces.rest.resources.PatientResource;

public class PatientResourceFromEntityAssembler {
    public static PatientResource toResourceFromEntity(Patient patient) {
        return new PatientResource(
                patient.getId(),
                patient.getMedicalRecordNumber().medicalRecordNumber(),
                patient.getFullName(),
                patient.getAdmissionDiagnosis(),
                patient.getAge(),
                patient.getBedLocation().unit(),
                patient.getBedLocation().bed(),
                patient.getAdmittedAt(),
                patient.isActive(),
                patient.getDischargedAt());
    }
}
