package com.digitalclinicalsystems.clinicalsync.platform.alerts.infrastructure.persistence.jpa.repositories;

import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.AlertSeverity;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.AlertStatus;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.TriggerSource;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.infrastructure.persistence.jpa.entities.AlertPersistenceEntity;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertPersistenceRepository extends
        JpaRepository<AlertPersistenceEntity, Long> {

    @Query("""
            select a from AlertPersistenceEntity a
            where (:patientId is null or a.patientId = :patientId)
              and (:status is null or a.status = :status)
              and (:severity is null or a.severity = :severity)
            order by a.raisedAt desc, a.id desc
            """)
    List<AlertPersistenceEntity> findAllByFilters(@Param("patientId") PatientId patientId,
                                                  @Param("status") AlertStatus status,
                                                  @Param("severity") AlertSeverity severity);

    boolean existsByTriggerSource(TriggerSource triggerSource);
}
