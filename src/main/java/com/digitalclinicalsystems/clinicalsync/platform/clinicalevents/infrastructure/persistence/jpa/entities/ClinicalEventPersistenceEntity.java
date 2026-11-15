package com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.infrastructure.persistence.jpa.entities;

import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.valueobjects.ClinicalEventSeverity;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.valueobjects.ClinicalEventType;
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
@Table(name = "clinical_events")
@Getter
@Setter
@NoArgsConstructor
public class ClinicalEventPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Convert(converter = PatientIdPersistenceConverter.class)
    @Column(name = "patient_id", nullable = false, updatable = false)
    private PatientId patientId;

    @Convert(converter = UserIdPersistenceConverter.class)
    @Column(name = "recorded_by", nullable = false, updatable = false)
    private UserId recordedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30, nullable = false, updatable = false)
    private ClinicalEventType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", length = 10, nullable = false, updatable = false)
    private ClinicalEventSeverity severity;

    @Column(name = "description", length = 2000, nullable = false, updatable = false)
    private String description;

    @Column(name = "medication", updatable = false)
    private String medication;

    @Column(name = "dose", updatable = false)
    private String dose;

    @Column(name = "related_order_id", updatable = false)
    private Long relatedOrderId;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    @PreUpdate
    @PreRemove
    private void rejectModification() {
        throw new IllegalStateException("Clinical events are immutable");
    }
}
