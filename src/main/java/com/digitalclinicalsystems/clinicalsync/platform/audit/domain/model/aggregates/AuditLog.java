package com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.aggregates;

import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.commands.CreateAuditLogCommand;
import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.valueobjects.ActionType;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.AbstractDomainAggregateRoot;

import java.time.Instant;

/**
 * Aggregate root of BC-06. Immutable: the log is append-only and entries are
 * created by the system when it consumes a domain event, never by a user.
 * Actor, timestamp, action type and affected resource are mandatory.
 */
public class AuditLog extends AbstractDomainAggregateRoot<AuditLog> {

    private Long id;
    private final ActionType actionType;
    private final Instant occurredAt;
    private final Long patientId;
    private final String affectedResource;
    private final String actorId;
    private final String metadata;

    public AuditLog(CreateAuditLogCommand command) {
        this(ActionType.fromEventName(command.eventName()), command.occurredAt(), command.patientId(),
                command.affectedResource(), command.actorId(), command.metadata());
    }

    private AuditLog(ActionType actionType, Instant occurredAt, Long patientId, String affectedResource,
                     String actorId, String metadata) {
        if (occurredAt == null) {
            throw new IllegalArgumentException("Every audit entry requires the moment the action occurred");
        }
        if (affectedResource == null || affectedResource.isBlank()) {
            throw new IllegalArgumentException("Every audit entry requires the affected resource");
        }
        if (actorId == null || actorId.isBlank()) {
            throw new IllegalArgumentException("Every audit entry requires the actor");
        }
        this.actionType = actionType;
        this.occurredAt = occurredAt;
        this.patientId = patientId;
        this.affectedResource = affectedResource;
        this.actorId = actorId;
        this.metadata = metadata == null || metadata.isBlank() ? "{}" : metadata;
    }

    public static AuditLog reconstitute(Long id, ActionType actionType, Instant occurredAt, Long patientId,
                                        String affectedResource, String actorId, String metadata) {
        var auditLog = new AuditLog(actionType, occurredAt, patientId, affectedResource, actorId, metadata);
        auditLog.id = id;
        return auditLog;
    }

    public Long getId() {
        return id;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    public Long getPatientId() {
        return patientId;
    }

    public String getAffectedResource() {
        return affectedResource;
    }

    public String getActorId() {
        return actorId;
    }

    public String getMetadata() {
        return metadata;
    }
}
