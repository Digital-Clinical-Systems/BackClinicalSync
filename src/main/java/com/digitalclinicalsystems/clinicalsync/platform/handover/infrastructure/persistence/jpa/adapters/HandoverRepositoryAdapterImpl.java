package com.digitalclinicalsystems.clinicalsync.platform.handover.infrastructure.persistence.jpa.adapters;

import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.aggregates.Handover;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.repositories.IHandoverRepository;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.valueobjects.HandoverStatus;
import com.digitalclinicalsystems.clinicalsync.platform.handover.infrastructure.persistence.jpa.entities.HandoverPersistenceEntity;
import com.digitalclinicalsystems.clinicalsync.platform.handover.infrastructure.persistence.jpa.repositories.HandoverPersistenceRepository;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class HandoverRepositoryAdapterImpl implements IHandoverRepository {

    private final HandoverPersistenceRepository handoverPersistenceRepository;

    public HandoverRepositoryAdapterImpl(HandoverPersistenceRepository handoverPersistenceRepository) {
        this.handoverPersistenceRepository = handoverPersistenceRepository;
    }

    @Override
    public List<Handover> findAllByFilters(PatientId patientId, UserId incomingNurseId, HandoverStatus status) {
        return handoverPersistenceRepository.findAllByFilters(patientId, incomingNurseId, status).stream()
                .map(HandoverRepositoryAdapterImpl::toDomain).toList();
    }

    @Override
    public List<Handover> findAll() {
        return handoverPersistenceRepository.findAll().stream().map(HandoverRepositoryAdapterImpl::toDomain).toList();
    }

    @Override
    public Handover save(Handover handover) {
        var entity = new HandoverPersistenceEntity();
        entity.setPatientId(handover.getPatientId());
        entity.setOutgoingNurseId(handover.getOutgoingNurseId());
        entity.setIncomingNurseId(handover.getIncomingNurseId());
        entity.setContent(handover.getContent());
        entity.setIssuedAt(handover.getIssuedAt());
        entity.setStatus(handover.getStatus());
        entity.setAcknowledgedAt(handover.getAcknowledgedAt());
        return toDomain(handoverPersistenceRepository.save(entity));
    }

    @Override
    public Handover update(Handover handover) {
        var entity = handoverPersistenceRepository.findById(handover.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Handover", handover.getId()));
        entity.setStatus(handover.getStatus());
        entity.setAcknowledgedAt(handover.getAcknowledgedAt());
        return toDomain(handoverPersistenceRepository.save(entity));
    }

    @Override
    public Optional<Handover> findById(Long id) {
        return handoverPersistenceRepository.findById(id).map(HandoverRepositoryAdapterImpl::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        handoverPersistenceRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        handoverPersistenceRepository.deleteAll();
    }

    private static Handover toDomain(HandoverPersistenceEntity entity) {
        return Handover.reconstitute(
                entity.getId(),
                entity.getPatientId(),
                entity.getOutgoingNurseId(),
                entity.getIncomingNurseId(),
                entity.getContent(),
                entity.getStatus(),
                entity.getIssuedAt(),
                entity.getAcknowledgedAt());
    }
}
