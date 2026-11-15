package com.digitalclinicalsystems.clinicalsync.platform.patients.infrastructure.persistence.jpa.adapters;

import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.aggregates.Patient;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.repositories.IPatientRepository;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.valueobjects.MedicalRecordNumber;
import com.digitalclinicalsystems.clinicalsync.platform.patients.infrastructure.persistence.jpa.entities.PatientPersistenceEntity;
import com.digitalclinicalsystems.clinicalsync.platform.patients.infrastructure.persistence.jpa.repositories.PatientPersistenceRepository;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class PatientRepositoryAdapterImpl implements IPatientRepository {

    private final PatientPersistenceRepository patientPersistenceRepository;

    public PatientRepositoryAdapterImpl(PatientPersistenceRepository patientPersistenceRepository) {
        this.patientPersistenceRepository = patientPersistenceRepository;
    }

    @Override
    public Optional<Patient> findByMedicalRecordNumber(MedicalRecordNumber medicalRecordNumber) {
        return patientPersistenceRepository.findByMedicalRecordNumber(medicalRecordNumber)
                .map(PatientRepositoryAdapterImpl::toDomain);
    }

    @Override
    public boolean existsByMedicalRecordNumber(MedicalRecordNumber medicalRecordNumber) {
        return patientPersistenceRepository.existsByMedicalRecordNumber(medicalRecordNumber);
    }

    @Override
    public List<Patient> findAllByActive(boolean active) {
        return patientPersistenceRepository.findAllByActive(active).stream()
                .map(PatientRepositoryAdapterImpl::toDomain).toList();
    }

    @Override
    public List<Patient> findAll() {
        return patientPersistenceRepository.findAll().stream().map(PatientRepositoryAdapterImpl::toDomain).toList();
    }

    @Override
    public Patient save(Patient patient) {
        var entity = new PatientPersistenceEntity();
        copyToEntity(patient, entity);
        return toDomain(patientPersistenceRepository.save(entity));
    }

    @Override
    public Patient update(Patient patient) {
        var entity = patientPersistenceRepository.findById(patient.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", patient.getId()));
        copyToEntity(patient, entity);
        return toDomain(patientPersistenceRepository.save(entity));
    }

    @Override
    public Optional<Patient> findById(Long id) {
        return patientPersistenceRepository.findById(id).map(PatientRepositoryAdapterImpl::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        patientPersistenceRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        patientPersistenceRepository.deleteAll();
    }

    private static void copyToEntity(Patient patient, PatientPersistenceEntity entity) {
        entity.setMedicalRecordNumber(patient.getMedicalRecordNumber());
        entity.setFullName(patient.getFullName());
        entity.setAdmissionDiagnosis(patient.getAdmissionDiagnosis());
        entity.setAge(patient.getAge());
        entity.setBedLocation(patient.getBedLocation());
        entity.setAdmittedAt(patient.getAdmittedAt());
        entity.setAdmittedBy(patient.getAdmittedBy());
        entity.setActive(patient.isActive());
        entity.setDischargedAt(patient.getDischargedAt());
        entity.setDischargedBy(patient.getDischargedBy());
    }

    private static Patient toDomain(PatientPersistenceEntity entity) {
        return Patient.reconstitute(
                entity.getId(),
                entity.getMedicalRecordNumber(),
                entity.getFullName(),
                entity.getAdmissionDiagnosis(),
                entity.getAge(),
                entity.getBedLocation(),
                entity.getAdmittedAt(),
                entity.getAdmittedBy(),
                entity.isActive(),
                entity.getDischargedAt(),
                entity.getDischargedBy());
    }
}
