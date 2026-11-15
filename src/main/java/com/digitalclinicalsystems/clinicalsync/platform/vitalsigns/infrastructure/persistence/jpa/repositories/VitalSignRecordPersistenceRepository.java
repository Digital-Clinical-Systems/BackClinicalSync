package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.infrastructure.persistence.jpa.repositories;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.infrastructure.persistence.jpa.entities.VitalSignRecordPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface VitalSignRecordPersistenceRepository extends
        JpaRepository<VitalSignRecordPersistenceEntity, Long> {

    @Query("""
            select v from VitalSignRecordPersistenceEntity v
            where (:patientId is null or v.patientId = :patientId)
              and (:from is null or v.measuredAt >= :from)
              and (:to is null or v.measuredAt <= :to)
            order by v.measuredAt asc, v.id asc
            """)
    List<VitalSignRecordPersistenceEntity> findAllByFilters(@Param("patientId") PatientId patientId,
                                                            @Param("from") Instant from,
                                                            @Param("to") Instant to);
}
