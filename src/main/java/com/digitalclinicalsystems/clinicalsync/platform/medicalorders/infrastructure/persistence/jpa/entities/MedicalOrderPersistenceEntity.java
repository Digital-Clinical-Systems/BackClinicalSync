package com.digitalclinicalsystems.clinicalsync.platform.medicalorders.infrastructure.persistence.jpa.entities;

import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.valueobjects.Dosage;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.valueobjects.OrderStatus;
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
@Table(name = "medical_orders")
@Getter
@Setter
@NoArgsConstructor
public class MedicalOrderPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Convert(converter = PatientIdPersistenceConverter.class)
    @Column(name = "patient_id", nullable = false, updatable = false)
    private PatientId patientId;

    @Convert(converter = UserIdPersistenceConverter.class)
    @Column(name = "prescribed_by", nullable = false, updatable = false)
    private UserId prescribedBy;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "medication", column = @Column(name = "medication", nullable = false, updatable = false)),
            @AttributeOverride(name = "dose", column = @Column(name = "dose", nullable = false, updatable = false)),
            @AttributeOverride(name = "route", column = @Column(name = "route", nullable = false, updatable = false)),
            @AttributeOverride(name = "frequency", column = @Column(name = "frequency", nullable = false, updatable = false))
    })
    private Dosage dosage;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 15, nullable = false)
    private OrderStatus status;

    @Column(name = "prescribed_at", nullable = false, updatable = false)
    private Instant prescribedAt;

    @Column(name = "replaces_order_id", updatable = false)
    private Long replacesOrderId;

    @Column(name = "superseded_by")
    private Long supersededBy;

    @Column(name = "fulfilled_at")
    private Instant fulfilledAt;

    @Convert(converter = UserIdPersistenceConverter.class)
    @Column(name = "fulfilled_by")
    private UserId fulfilledBy;
}
