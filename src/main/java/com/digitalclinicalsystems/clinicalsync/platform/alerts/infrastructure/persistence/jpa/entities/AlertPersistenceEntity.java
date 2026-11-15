package com.digitalclinicalsystems.clinicalsync.platform.alerts.infrastructure.persistence.jpa.entities;

import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.AlertSeverity;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.AlertStatus;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.TriggerSource;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.infrastructure.persistence.jpa.converters.TriggerSourcePersistenceConverter;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.infrastructure.persistence.jpa.AuditableAbstractPersistenceEntity;
import com.digitalclinicalsystems.clinicalsync.platform.shared.infrastructure.persistence.jpa.converters.PatientIdPersistenceConverter;
import com.digitalclinicalsystems.clinicalsync.platform.shared.infrastructure.persistence.jpa.converters.UserIdPersistenceConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "alerts")
@Getter
@Setter
@NoArgsConstructor
public class AlertPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Convert(converter = PatientIdPersistenceConverter.class)
    @Column(name = "patient_id", nullable = false, updatable = false)
    private PatientId patientId;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", length = 10, nullable = false, updatable = false)
    private AlertSeverity severity;

    @Convert(converter = TriggerSourcePersistenceConverter.class)
    @Column(name = "trigger_source", length = 100, nullable = false, unique = true, updatable = false)
    private TriggerSource triggerSource;

    @Column(name = "reason", length = 1000, nullable = false, updatable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 15, nullable = false)
    private AlertStatus status;

    @Column(name = "raised_at", nullable = false, updatable = false)
    private Instant raisedAt;

    @Convert(converter = UserIdPersistenceConverter.class)
    @Column(name = "acknowledged_by")
    private UserId acknowledgedBy;

    @Column(name = "acknowledged_at")
    private Instant acknowledgedAt;

    @Convert(converter = UserIdPersistenceConverter.class)
    @Column(name = "resolved_by")
    private UserId resolvedBy;

    @Column(name = "resolved_at")
    private Instant resolvedAt;
}
