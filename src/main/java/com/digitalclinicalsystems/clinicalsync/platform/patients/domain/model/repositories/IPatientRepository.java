package com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.repositories;

import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.aggregates.Patient;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.valueobjects.MedicalRecordNumber;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.repositories.ICrudService;

import java.util.List;
import java.util.Optional;

public interface IPatientRepository extends ICrudService<Patient, Long> {
    Optional<Patient> findByMedicalRecordNumber(MedicalRecordNumber medicalRecordNumber);
    boolean existsByMedicalRecordNumber(MedicalRecordNumber medicalRecordNumber);
    List<Patient> findAllByActive(boolean active);
}
