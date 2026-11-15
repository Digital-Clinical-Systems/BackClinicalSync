package com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.aggregates;

import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.commands.RaiseAlertCommand;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.events.AlertAcknowledgedEvent;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.events.AlertRaisedEvent;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.events.AlertResolvedEvent;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.AlertSeverity;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.AlertStatus;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.TriggerSource;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.AbstractDomainAggregateRoot;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.BusinessRuleViolationException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;

import java.time.Instant;

/**
 * Aggregate root of BC-04.
 * Invariants: every alert references the source that triggered it; it cannot be
 * resolved without being acknowledged first; acknowledging requires the
 * responsible user; a resolved alert never goes back to a previous state.
 */
public class Alert extends AbstractDomainAggregateRoot<Alert> {

    private Long id;
    private PatientId patientId;
    private AlertSeverity severity;
    private TriggerSource triggerSource;
    private String reason;
    private AlertStatus status;
    private Instant raisedAt;
    private UserId acknowledgedBy;
    private Instant acknowledgedAt;
    private UserId resolvedBy;
    private Instant resolvedAt;

    private Alert() {
    }

    public Alert(RaiseAlertCommand command) {
        if (command.patientId() == null) {
            throw new IllegalArgumentException("An alert must reference a patient");
        }
        if (command.severity() == null) {
            throw new IllegalArgumentException("An alert requires a severity");
        }
        this.triggerSource = new TriggerSource(command.triggerSource());
        this.patientId = command.patientId();
        this.severity = command.severity();
        this.reason = command.reason() == null || command.reason().isBlank() ? "Out of threshold value" : command.reason().trim();
        this.status = AlertStatus.OPEN;
        this.raisedAt = Instant.now();
    }

    public static Alert reconstitute(Long id, PatientId patientId, AlertSeverity severity, TriggerSource triggerSource,
                                     String reason, AlertStatus status, Instant raisedAt, UserId acknowledgedBy,
                                     Instant acknowledgedAt, UserId resolvedBy, Instant resolvedAt) {
        var alert = new Alert();
        alert.id = id;
        alert.patientId = patientId;
        alert.severity = severity;
        alert.triggerSource = triggerSource;
        alert.reason = reason;
        alert.status = status;
        alert.raisedAt = raisedAt;
        alert.acknowledgedBy = acknowledgedBy;
        alert.acknowledgedAt = acknowledgedAt;
        alert.resolvedBy = resolvedBy;
        alert.resolvedAt = resolvedAt;
        return alert;
    }

    /** Records the raised event once the alert has an identity. */
    public void registerCreationEvents() {
        registerDomainEvents(new AlertRaisedEvent(
                id, patientId.patientId(), severity.name(), triggerSource.triggerSource(), raisedAt));
    }

    public void acknowledge(UserId by) {
        if (by == null) {
            throw new IllegalArgumentException("Acknowledging an alert requires the responsible user");
        }
        if (status != AlertStatus.OPEN) {
            throw new BusinessRuleViolationException("Only an open alert can be acknowledged (current status: %s)".formatted(status));
        }
        this.status = AlertStatus.ACKNOWLEDGED;
        this.acknowledgedBy = by;
        this.acknowledgedAt = Instant.now();
        registerDomainEvents(new AlertAcknowledgedEvent(id, patientId.patientId(), by.userId(), acknowledgedAt));
    }

    public void resolve(UserId by) {
        if (by == null) {
            throw new IllegalArgumentException("Resolving an alert requires the responsible user");
        }
        if (status == AlertStatus.RESOLVED) {
            throw new BusinessRuleViolationException("A resolved alert cannot change its state");
        }
        if (status != AlertStatus.ACKNOWLEDGED) {
            throw new BusinessRuleViolationException("An alert cannot be resolved before being acknowledged");
        }
        this.status = AlertStatus.RESOLVED;
        this.resolvedBy = by;
        this.resolvedAt = Instant.now();
        registerDomainEvents(new AlertResolvedEvent(id, patientId.patientId(), by.userId(), resolvedAt));
    }

    public Long getId() {
        return id;
    }

    public PatientId getPatientId() {
        return patientId;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public TriggerSource getTriggerSource() {
        return triggerSource;
    }

    public String getReason() {
        return reason;
    }

    public AlertStatus getStatus() {
        return status;
    }

    public Instant getRaisedAt() {
        return raisedAt;
    }

    public UserId getAcknowledgedBy() {
        return acknowledgedBy;
    }

    public Instant getAcknowledgedAt() {
        return acknowledgedAt;
    }

    public UserId getResolvedBy() {
        return resolvedBy;
    }

    public Instant getResolvedAt() {
        return resolvedAt;
    }
}
