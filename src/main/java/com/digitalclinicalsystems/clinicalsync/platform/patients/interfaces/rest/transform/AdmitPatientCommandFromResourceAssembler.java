package com.digitalclinicalsystems.clinicalsync.platform.patients.interfaces.rest.transform;

import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.commands.AdmitPatientCommand;
import com.digitalclinicalsystems.clinicalsync.platform.patients.interfaces.rest.resources.CreatePatientResource;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;

public class AdmitPatientCommandFromResourceAssembler {
    public static AdmitPatientCommand toCommandFromResource(CreatePatientResource resource, UserId admittedBy) {
        return new AdmitPatientCommand(
                resource.medicalRecordNumber(),
                resource.fullName(),
                resource.admissionDiagnosis(),
                resource.age(),
                resource.unit(),
                resource.bed(),
                admittedBy);
    }
}
