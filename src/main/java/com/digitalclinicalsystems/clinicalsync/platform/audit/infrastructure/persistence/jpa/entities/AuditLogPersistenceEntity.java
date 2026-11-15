package com.digitalclinicalsystems.clinicalsync.platform.audit.infrastructure.persistence.jpa.entities;

import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.valueobjects.ActionType;
import com.digitalclinicalsystems.clinicalsync.platform.audit.infrastructure.persistence.jpa.converters.ActionTypePersistenceConverter;
import com.digitalclinicalsystems.clinicalsync.platform.shared.infrastructure.persistence.jpa.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Append-only row: every column is non-updatable and update or delete attempts are rejected.
 */
@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
public class AuditLogPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Convert(converter = ActionTypePersistenceConverter.class)
    @Column(name = "action_type", length = 60, nullable = false, updatable = false)
    private ActionType actionType;

    @Column(name = "occurred_at", nullable = false, updatable = false)
    private Instant occurredAt;

    @Column(name = "patient_id", updatable = false)
    private Long patientId;

    @Column(name = "affected_resource", length = 100, nullable = false, updatable = false)
    private String affectedResource;

    @Column(name = "actor_id", length = 50, nullable = false, updatable = false)
    private String actorId;

    @Column(name = "metadata", columnDefinition = "json", nullable = false, updatable = false)
    private String metadata;

    @PreUpdate
    @PreRemove
    private void rejectModification() {
        throw new IllegalStateException("Audit logs are append-only");
    }
}
