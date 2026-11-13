package com.digitalclinicalsystems.clinicalsync.platform.audit.infrastructure.persistence.jpa.repositories;

import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.valueobjects.ActionType;
import com.digitalclinicalsystems.clinicalsync.platform.audit.infrastructure.persistence.jpa.entities.AuditLogPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface AuditLogPersistenceRepository extends
        JpaRepository<AuditLogPersistenceEntity, Long> {

    @Query("""
            select a from AuditLogPersistenceEntity a
            where (:patientId is null or a.patientId = :patientId)
              and (:actionType is null or a.actionType = :actionType)
              and (:from is null or a.occurredAt >= :from)
              and (:to is null or a.occurredAt <= :to)
            order by a.occurredAt asc, a.id asc
            """)
    List<AuditLogPersistenceEntity> findAllByFilters(@Param("patientId") Long patientId,
                                                     @Param("actionType") ActionType actionType,
                                                     @Param("from") Instant from,
                                                     @Param("to") Instant to);
}
