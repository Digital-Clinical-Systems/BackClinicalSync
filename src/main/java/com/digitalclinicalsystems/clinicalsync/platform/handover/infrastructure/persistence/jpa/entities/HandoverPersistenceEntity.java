package com.digitalclinicalsystems.clinicalsync.platform.handover.infrastructure.persistence.jpa.entities;

import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.valueobjects.HandoverStatus;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.valueobjects.SbarContent;
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
@Table(name = "handovers")
@Getter
@Setter
@NoArgsConstructor
public class HandoverPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Convert(converter = PatientIdPersistenceConverter.class)
    @Column(name = "patient_id", nullable = false, updatable = false)
    private PatientId patientId;

    @Convert(converter = UserIdPersistenceConverter.class)
    @Column(name = "outgoing_nurse_id", nullable = false, updatable = false)
    private UserId outgoingNurseId;

    @Convert(converter = UserIdPersistenceConverter.class)
    @Column(name = "incoming_nurse_id", nullable = false, updatable = false)
    private UserId incomingNurseId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "situation", column = @Column(name = "situation", length = 4000, nullable = false, updatable = false)),
            @AttributeOverride(name = "background", column = @Column(name = "background", length = 4000, nullable = false, updatable = false)),
            @AttributeOverride(name = "assessment", column = @Column(name = "assessment", length = 4000, nullable = false, updatable = false)),
            @AttributeOverride(name = "recommendation", column = @Column(name = "recommendation", length = 4000, nullable = false, updatable = false))
    })
    private SbarContent content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 15, nullable = false)
    private HandoverStatus status;

    @Column(name = "issued_at", nullable = false, updatable = false)
    private Instant issuedAt;

    @Column(name = "acknowledged_at")
    private Instant acknowledgedAt;
}
