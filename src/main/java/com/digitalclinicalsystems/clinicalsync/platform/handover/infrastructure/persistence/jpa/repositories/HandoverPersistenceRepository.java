package com.digitalclinicalsystems.clinicalsync.platform.handover.infrastructure.persistence.jpa.repositories;

import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.valueobjects.HandoverStatus;
import com.digitalclinicalsystems.clinicalsync.platform.handover.infrastructure.persistence.jpa.entities.HandoverPersistenceEntity;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HandoverPersistenceRepository extends
        JpaRepository<HandoverPersistenceEntity, Long> {

    @Query("""
            select h from HandoverPersistenceEntity h
            where (:patientId is null or h.patientId = :patientId)
              and (:incomingNurseId is null or h.incomingNurseId = :incomingNurseId)
              and (:status is null or h.status = :status)
            order by h.issuedAt desc, h.id desc
            """)
    List<HandoverPersistenceEntity> findAllByFilters(@Param("patientId") PatientId patientId,
                                                     @Param("incomingNurseId") UserId incomingNurseId,
                                                     @Param("status") HandoverStatus status);
}
