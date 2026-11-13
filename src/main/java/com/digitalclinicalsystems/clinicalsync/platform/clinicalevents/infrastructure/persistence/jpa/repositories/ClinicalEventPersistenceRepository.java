package com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.infrastructure.persistence.jpa.repositories;

import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.valueobjects.ClinicalEventType;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.infrastructure.persistence.jpa.entities.ClinicalEventPersistenceEntity;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ClinicalEventPersistenceRepository extends
        JpaRepository<ClinicalEventPersistenceEntity, Long> {

    @Query("""
            select c from ClinicalEventPersistenceEntity c
            where (:patientId is null or c.patientId = :patientId)
              and (:type is null or c.type = :type)
              and (:from is null or c.occurredAt >= :from)
              and (:to is null or c.occurredAt <= :to)
            order by c.occurredAt asc, c.id asc
            """)
    List<ClinicalEventPersistenceEntity> findAllByFilters(@Param("patientId") PatientId patientId,
                                                          @Param("type") ClinicalEventType type,
                                                          @Param("from") Instant from,
                                                          @Param("to") Instant to);
}
