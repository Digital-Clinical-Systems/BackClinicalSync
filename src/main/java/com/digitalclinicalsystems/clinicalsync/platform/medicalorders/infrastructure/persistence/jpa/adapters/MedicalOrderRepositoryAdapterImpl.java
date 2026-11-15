package com.digitalclinicalsystems.clinicalsync.platform.medicalorders.infrastructure.persistence.jpa.adapters;

import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.aggregates.MedicalOrder;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.repositories.IMedicalOrderRepository;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.valueobjects.OrderStatus;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.infrastructure.persistence.jpa.entities.MedicalOrderPersistenceEntity;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.infrastructure.persistence.jpa.repositories.MedicalOrderPersistenceRepository;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MedicalOrderRepositoryAdapterImpl implements IMedicalOrderRepository {

    private final MedicalOrderPersistenceRepository medicalOrderPersistenceRepository;

    public MedicalOrderRepositoryAdapterImpl(MedicalOrderPersistenceRepository medicalOrderPersistenceRepository) {
        this.medicalOrderPersistenceRepository = medicalOrderPersistenceRepository;
    }

    @Override
    public List<MedicalOrder> findAllByFilters(PatientId patientId, OrderStatus status) {
        return medicalOrderPersistenceRepository.findAllByFilters(patientId, status).stream()
                .map(MedicalOrderRepositoryAdapterImpl::toDomain).toList();
    }

    @Override
    public List<MedicalOrder> findAll() {
        return medicalOrderPersistenceRepository.findAll().stream().map(MedicalOrderRepositoryAdapterImpl::toDomain).toList();
    }

    @Override
    public MedicalOrder save(MedicalOrder order) {
        var entity = new MedicalOrderPersistenceEntity();
        entity.setPatientId(order.getPatientId());
        entity.setPrescribedBy(order.getPrescribedBy());
        entity.setDosage(order.getDosage());
        entity.setPrescribedAt(order.getPrescribedAt());
        entity.setReplacesOrderId(order.getReplacesOrderId());
        copyStateToEntity(order, entity);
        return toDomain(medicalOrderPersistenceRepository.save(entity));
    }

    @Override
    public MedicalOrder update(MedicalOrder order) {
        var entity = medicalOrderPersistenceRepository.findById(order.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Medical order", order.getId()));
        copyStateToEntity(order, entity);
        return toDomain(medicalOrderPersistenceRepository.save(entity));
    }

    @Override
    public Optional<MedicalOrder> findById(Long id) {
        return medicalOrderPersistenceRepository.findById(id).map(MedicalOrderRepositoryAdapterImpl::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        medicalOrderPersistenceRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        medicalOrderPersistenceRepository.deleteAll();
    }

    private static void copyStateToEntity(MedicalOrder order, MedicalOrderPersistenceEntity entity) {
        entity.setStatus(order.getStatus());
        entity.setSupersededBy(order.getSupersededBy());
        entity.setFulfilledAt(order.getFulfilledAt());
        entity.setFulfilledBy(order.getFulfilledBy());
    }

    private static MedicalOrder toDomain(MedicalOrderPersistenceEntity entity) {
        return MedicalOrder.reconstitute(
                entity.getId(),
                entity.getPatientId(),
                entity.getPrescribedBy(),
                entity.getDosage(),
                entity.getStatus(),
                entity.getPrescribedAt(),
                entity.getReplacesOrderId(),
                entity.getSupersededBy(),
                entity.getFulfilledAt(),
                entity.getFulfilledBy());
    }
}
