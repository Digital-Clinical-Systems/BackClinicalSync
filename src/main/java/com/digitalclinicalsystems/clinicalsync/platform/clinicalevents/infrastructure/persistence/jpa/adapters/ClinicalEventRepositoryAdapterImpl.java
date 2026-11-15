package com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.infrastructure.persistence.jpa.adapters;

import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.aggregates.ClinicalEvent;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.repositories.IClinicalEventRepository;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.valueobjects.ClinicalEventType;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.infrastructure.persistence.jpa.entities.ClinicalEventPersistenceEntity;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.infrastructure.persistence.jpa.repositories.ClinicalEventPersistenceRepository;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class ClinicalEventRepositoryAdapterImpl implements IClinicalEventRepository {

    private final ClinicalEventPersistenceRepository clinicalEventPersistenceRepository;

    public ClinicalEventRepositoryAdapterImpl(ClinicalEventPersistenceRepository clinicalEventPersistenceRepository) {
        this.clinicalEventPersistenceRepository = clinicalEventPersistenceRepository;
    }

    @Override
    public ClinicalEvent save(ClinicalEvent clinicalEvent) {
        var entity = new ClinicalEventPersistenceEntity();
        entity.setPatientId(clinicalEvent.getPatientId());
        entity.setRecordedBy(clinicalEvent.getRecordedBy());
        entity.setType(clinicalEvent.getType());
        entity.setSeverity(clinicalEvent.getSeverity());
        entity.setDescription(clinicalEvent.getDescription());
        entity.setMedication(clinicalEvent.getMedication());
        entity.setDose(clinicalEvent.getDose());
        entity.setRelatedOrderId(clinicalEvent.getRelatedOrderId());
        entity.setOccurredAt(clinicalEvent.getOccurredAt());
        return toDomain(clinicalEventPersistenceRepository.save(entity));
    }

    @Override
    public Optional<ClinicalEvent> findById(Long id) {
        return clinicalEventPersistenceRepository.findById(id).map(ClinicalEventRepositoryAdapterImpl::toDomain);
    }

    @Override
    public List<ClinicalEvent> findAllByFilters(PatientId patientId, ClinicalEventType type, Instant from, Instant to) {
        return clinicalEventPersistenceRepository.findAllByFilters(patientId, type, from, to).stream()
                .map(ClinicalEventRepositoryAdapterImpl::toDomain)
                .toList();
    }

    private static ClinicalEvent toDomain(ClinicalEventPersistenceEntity entity) {
        return ClinicalEvent.reconstitute(
                entity.getId(),
                entity.getPatientId(),
                entity.getRecordedBy(),
                entity.getType(),
                entity.getSeverity(),
                entity.getDescription(),
                entity.getMedication(),
                entity.getDose(),
                entity.getRelatedOrderId(),
                entity.getOccurredAt());
    }
}
