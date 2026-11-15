package com.digitalclinicalsystems.clinicalsync.platform.patients.infrastructure.persistence.jpa.repositories;

import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.valueobjects.MedicalRecordNumber;
import com.digitalclinicalsystems.clinicalsync.platform.patients.infrastructure.persistence.jpa.entities.PatientPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientPersistenceRepository extends
        JpaRepository<PatientPersistenceEntity, Long> {
    Optional<PatientPersistenceEntity> findByMedicalRecordNumber(MedicalRecordNumber medicalRecordNumber);
    boolean existsByMedicalRecordNumber(MedicalRecordNumber medicalRecordNumber);
    List<PatientPersistenceEntity> findAllByActive(boolean active);
}
