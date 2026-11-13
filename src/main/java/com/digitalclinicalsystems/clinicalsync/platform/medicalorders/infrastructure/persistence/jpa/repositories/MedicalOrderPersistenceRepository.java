package com.digitalclinicalsystems.clinicalsync.platform.medicalorders.infrastructure.persistence.jpa.repositories;

import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.valueobjects.OrderStatus;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.infrastructure.persistence.jpa.entities.MedicalOrderPersistenceEntity;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalOrderPersistenceRepository extends
        JpaRepository<MedicalOrderPersistenceEntity, Long> {

    @Query("""
            select m from MedicalOrderPersistenceEntity m
            where (:patientId is null or m.patientId = :patientId)
              and (:status is null or m.status = :status)
            order by m.prescribedAt desc, m.id desc
            """)
    List<MedicalOrderPersistenceEntity> findAllByFilters(@Param("patientId") PatientId patientId,
                                                         @Param("status") OrderStatus status);
}
