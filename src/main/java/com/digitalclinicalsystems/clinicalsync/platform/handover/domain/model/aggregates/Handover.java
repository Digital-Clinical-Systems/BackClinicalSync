package com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.aggregates;

import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.commands.IssueHandoverCommand;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.events.HandoverAcknowledgedEvent;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.events.SbarHandoverIssuedEvent;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.valueobjects.HandoverStatus;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.valueobjects.SbarContent;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.AbstractDomainAggregateRoot;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ActionNotAllowedException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.BusinessRuleViolationException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;

import java.time.Instant;

/**
 * Aggregate root of BC-05.
 * Invariants: the four SBAR sections are complete; the handover is issued by
 * nursing staff; the incoming nurse differs from the outgoing one; only the
 * incoming nurse acknowledges it, and only once.
 */
public class Handover extends AbstractDomainAggregateRoot<Handover> {

    private Long id;
    private PatientId patientId;
    private UserId outgoingNurseId;
    private UserId incomingNurseId;
    private SbarContent content;
    private HandoverStatus status;
    private Instant issuedAt;
    private Instant acknowledgedAt;

    private Handover() {
    }

    public Handover(IssueHandoverCommand command) {
        if (command.patientId() == null || command.outgoingNurseId() == null || command.incomingNurseId() == null) {
            throw new IllegalArgumentException("A handover requires the patient and both nurses");
        }
        if (command.outgoingNurseRoles() == null || !command.outgoingNurseRoles().contains(Roles.NURSE)) {
            throw new ActionNotAllowedException("The SBAR handover is issued by the nursing staff handing over the shift");
        }
        if (command.outgoingNurseId().equals(command.incomingNurseId())) {
            throw new IllegalArgumentException("The incoming nurse must be different from the outgoing nurse");
        }
        this.content = new SbarContent(command.situation(), command.background(), command.assessment(), command.recommendation());
        this.patientId = command.patientId();
        this.outgoingNurseId = command.outgoingNurseId();
        this.incomingNurseId = command.incomingNurseId();
        this.status = HandoverStatus.ISSUED;
        this.issuedAt = Instant.now();
    }

    public static Handover reconstitute(Long id, PatientId patientId, UserId outgoingNurseId, UserId incomingNurseId,
                                        SbarContent content, HandoverStatus status, Instant issuedAt, Instant acknowledgedAt) {
        if (outgoingNurseId.equals(incomingNurseId)) {
            throw new IllegalArgumentException("The incoming nurse must be different from the outgoing nurse");
        }
        var handover = new Handover();
        handover.id = id;
        handover.patientId = patientId;
        handover.outgoingNurseId = outgoingNurseId;
        handover.incomingNurseId = incomingNurseId;
        handover.content = content;
        handover.status = status;
        handover.issuedAt = issuedAt;
        handover.acknowledgedAt = acknowledgedAt;
        return handover;
    }

    /** Records the issued event once the handover has an identity. */
    public void registerCreationEvents() {
        registerDomainEvents(new SbarHandoverIssuedEvent(
                id, patientId.patientId(), outgoingNurseId.userId(), incomingNurseId.userId(), issuedAt));
    }

    public void acknowledge(UserId by) {
        if (status == HandoverStatus.ACKNOWLEDGED) {
            throw new BusinessRuleViolationException("The handover has already been acknowledged");
        }
        if (by == null || !by.equals(incomingNurseId)) {
            throw new ActionNotAllowedException("Only the incoming nurse can acknowledge the handover");
        }
        this.status = HandoverStatus.ACKNOWLEDGED;
        this.acknowledgedAt = Instant.now();
        registerDomainEvents(new HandoverAcknowledgedEvent(id, patientId.patientId(), by.userId(), acknowledgedAt));
    }

    public boolean isPending() {
        return status == HandoverStatus.ISSUED;
    }

    public Long getId() {
        return id;
    }

    public PatientId getPatientId() {
        return patientId;
    }

    public UserId getOutgoingNurseId() {
        return outgoingNurseId;
    }

    public UserId getIncomingNurseId() {
        return incomingNurseId;
    }

    public SbarContent getContent() {
        return content;
    }

    public HandoverStatus getStatus() {
        return status;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public Instant getAcknowledgedAt() {
        return acknowledgedAt;
    }
}
