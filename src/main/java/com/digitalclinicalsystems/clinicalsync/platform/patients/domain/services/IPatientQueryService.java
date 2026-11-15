package com.digitalclinicalsystems.clinicalsync.platform.patients.domain.services;

import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.aggregates.Patient;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.queries.GetAllPatientsQuery;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.queries.GetPatientByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.queries.GetPatientByMedicalRecordNumberQuery;

import java.util.List;
import java.util.Optional;

public interface IPatientQueryService {
    List<Patient> handle(GetAllPatientsQuery query);
    Optional<Patient> handle(GetPatientByIdQuery query);
    Optional<Patient> handle(GetPatientByMedicalRecordNumberQuery query);
}
