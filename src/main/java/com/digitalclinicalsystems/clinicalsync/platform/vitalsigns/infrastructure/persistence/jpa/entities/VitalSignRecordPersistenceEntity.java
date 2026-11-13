package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.infrastructure.persistence.jpa.entities;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.infrastructure.persistence.jpa.AuditableAbstractPersistenceEntity;
import com.digitalclinicalsystems.clinicalsync.platform.shared.infrastructure.persistence.jpa.converters.PatientIdPersistenceConverter;
import com.digitalclinicalsystems.clinicalsync.platform.shared.infrastructure.persistence.jpa.converters.UserIdPersistenceConverter;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.valueobjects.BloodPressure;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.valueobjects.RiskLevel;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Immutable row: every column is non-updatable and any update attempt is rejected.
 */
@Entity
@Table(name = "vital_sign_records")
@Getter
@Setter
@NoArgsConstructor
public class VitalSignRecordPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Convert(converter = PatientIdPersistenceConverter.class)
    @Column(name = "patient_id", nullable = false, updatable = false)
    private PatientId patientId;

    @Convert(converter = UserIdPersistenceConverter.class)
    @Column(name = "recorded_by", nullable = false, updatable = false)
    private UserId recordedBy;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "systolic", column = @Column(name = "systolic", nullable = false, updatable = false)),
            @AttributeOverride(name = "diastolic", column = @Column(name = "diastolic", nullable = false, updatable = false))
    })
    private BloodPressure bloodPressure;

    @Column(name = "heart_rate", nullable = false, updatable = false)
    private Integer heartRate;

    @Column(name = "oxygen_saturation", nullable = false, updatable = false)
    private Integer oxygenSaturation;

    @Column(name = "temperature", nullable = false, updatable = false)
    private Double temperature;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", length = 10, nullable = false, updatable = false)
    private RiskLevel riskLevel;

    @Column(name = "corrects_record_id", updatable = false)
    private Long correctsRecordId;

    @Column(name = "measured_at", nullable = false, updatable = false)
    private Instant measuredAt;

    @PreUpdate
    @PreRemove
    private void rejectModification() {
        throw new IllegalStateException("Vital sign records are immutable");
    }
}
