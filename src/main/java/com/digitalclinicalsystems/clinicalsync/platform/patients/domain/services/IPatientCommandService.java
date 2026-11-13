package com.digitalclinicalsystems.clinicalsync.platform.patients.domain.services;

import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.aggregates.Patient;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.commands.AdmitPatientCommand;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.commands.DischargePatientCommand;

import java.util.Optional;

public interface IPatientCommandService {
    Optional<Patient> handle(AdmitPatientCommand command);
    Optional<Patient> handle(DischargePatientCommand command);
}
