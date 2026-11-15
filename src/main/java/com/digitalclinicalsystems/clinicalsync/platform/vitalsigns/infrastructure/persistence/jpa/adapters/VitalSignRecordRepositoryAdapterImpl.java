package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.infrastructure.persistence.jpa.adapters;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.aggregates.VitalSignRecord;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.repositories.IVitalSignRecordRepository;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.infrastructure.persistence.jpa.entities.VitalSignRecordPersistenceEntity;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.infrastructure.persistence.jpa.repositories.VitalSignRecordPersistenceRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class VitalSignRecordRepositoryAdapterImpl implements IVitalSignRecordRepository {

    private final VitalSignRecordPersistenceRepository vitalSignRecordPersistenceRepository;

    public VitalSignRecordRepositoryAdapterImpl(VitalSignRecordPersistenceRepository vitalSignRecordPersistenceRepository) {
        this.vitalSignRecordPersistenceRepository = vitalSignRecordPersistenceRepository;
    }

    @Override
    public VitalSignRecord save(VitalSignRecord record) {
        var entity = new VitalSignRecordPersistenceEntity();
        entity.setPatientId(record.getPatientId());
        entity.setRecordedBy(record.getRecordedBy());
        entity.setBloodPressure(record.getBloodPressure());
        entity.setHeartRate(record.getHeartRate());
        entity.setOxygenSaturation(record.getOxygenSaturation());
        entity.setTemperature(record.getTemperature());
        entity.setRiskLevel(record.getRiskLevel());
        entity.setCorrectsRecordId(record.getCorrectsRecordId());
        entity.setMeasuredAt(record.getMeasuredAt());
        return toDomain(vitalSignRecordPersistenceRepository.save(entity));
    }

    @Override
    public Optional<VitalSignRecord> findById(Long id) {
        return vitalSignRecordPersistenceRepository.findById(id).map(VitalSignRecordRepositoryAdapterImpl::toDomain);
    }

    @Override
    public List<VitalSignRecord> findAllByFilters(PatientId patientId, Instant from, Instant to) {
        return vitalSignRecordPersistenceRepository.findAllByFilters(patientId, from, to).stream()
                .map(VitalSignRecordRepositoryAdapterImpl::toDomain)
                .toList();
    }

    private static VitalSignRecord toDomain(VitalSignRecordPersistenceEntity entity) {
        return VitalSignRecord.reconstitute(
                entity.getId(),
                entity.getPatientId(),
                entity.getRecordedBy(),
                entity.getBloodPressure(),
                entity.getHeartRate(),
                entity.getOxygenSaturation(),
                entity.getTemperature(),
                entity.getCorrectsRecordId(),
                entity.getMeasuredAt());
    }
}
