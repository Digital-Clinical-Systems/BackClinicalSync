package com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.aggregates;

import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.commands.IssueMedicalOrderCommand;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.events.MedicalOrderFulfilledEvent;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.events.MedicalOrderIssuedEvent;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.events.MedicalOrderSupersededEvent;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.valueobjects.Dosage;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.valueobjects.OrderStatus;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.AbstractDomainAggregateRoot;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ActionNotAllowedException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.BusinessRuleViolationException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;

import java.time.Instant;
import java.util.Set;

/**
 * Aggregate root of BC-07.
 * Invariants: only a physician issues an order; every order has a prescriber and
 * a patient; a replaced order stays in the history as SUPERSEDED and never
 * becomes active again; fulfillment is recorded by nursing staff, once, only on
 * an active order and never by the prescriber.
 */
public class MedicalOrder extends AbstractDomainAggregateRoot<MedicalOrder> {

    private Long id;
    private PatientId patientId;
    private UserId prescribedBy;
    private Dosage dosage;
    private OrderStatus status;
    private Instant prescribedAt;
    private Long replacesOrderId;
    private Long supersededBy;
    private Instant fulfilledAt;
    private UserId fulfilledBy;

    private MedicalOrder() {
    }

    public MedicalOrder(IssueMedicalOrderCommand command) {
        if (command.prescriberRoles() == null || !command.prescriberRoles().contains(Roles.PHYSICIAN)) {
            throw new ActionNotAllowedException("Only a physician can issue a medical order");
        }
        if (command.patientId() == null || command.prescribedBy() == null) {
            throw new IllegalArgumentException("A medical order requires a prescriber and a patient");
        }
        this.dosage = new Dosage(command.medication(), command.dose(), command.route(), command.frequency());
        this.patientId = command.patientId();
        this.prescribedBy = command.prescribedBy();
        this.replacesOrderId = command.replacesOrderId();
        this.status = OrderStatus.ACTIVE;
        this.prescribedAt = Instant.now();
    }

    public static MedicalOrder reconstitute(Long id, PatientId patientId, UserId prescribedBy, Dosage dosage,
                                            OrderStatus status, Instant prescribedAt, Long replacesOrderId,
                                            Long supersededBy, Instant fulfilledAt, UserId fulfilledBy) {
        var order = new MedicalOrder();
        order.id = id;
        order.patientId = patientId;
        order.prescribedBy = prescribedBy;
        order.dosage = dosage;
        order.status = status;
        order.prescribedAt = prescribedAt;
        order.replacesOrderId = replacesOrderId;
        order.supersededBy = supersededBy;
        order.fulfilledAt = fulfilledAt;
        order.fulfilledBy = fulfilledBy;
        return order;
    }

    /** Records the issued event once the order has an identity. */
    public void registerCreationEvents() {
        registerDomainEvents(new MedicalOrderIssuedEvent(
                id, patientId.patientId(), prescribedBy.userId(), replacesOrderId, prescribedAt));
    }

    public void supersede(MedicalOrder replacement) {
        if (status != OrderStatus.ACTIVE) {
            throw new BusinessRuleViolationException("Only an active medical order can be replaced");
        }
        if (!replacement.getPatientId().equals(patientId)) {
            throw new IllegalArgumentException("The replacing order must belong to the same patient");
        }
        this.status = OrderStatus.SUPERSEDED;
        this.supersededBy = replacement.getId();
        registerDomainEvents(new MedicalOrderSupersededEvent(
                id, patientId.patientId(), replacement.getId(), replacement.getPrescribedBy().userId(), Instant.now()));
    }

    public void fulfill(UserId by, Set<Roles> byRoles) {
        if (byRoles == null || !byRoles.contains(Roles.NURSE)) {
            throw new ActionNotAllowedException("Fulfillment of a medical order is recorded by the nursing staff who administers it");
        }
        if (status != OrderStatus.ACTIVE) {
            throw new BusinessRuleViolationException("Only an active medical order can be fulfilled");
        }
        if (fulfilledAt != null) {
            throw new BusinessRuleViolationException("The medical order already has its fulfillment recorded");
        }
        if (prescribedBy.equals(by)) {
            throw new ActionNotAllowedException("The prescriber cannot record the fulfillment of their own order");
        }
        this.fulfilledAt = Instant.now();
        this.fulfilledBy = by;
        registerDomainEvents(new MedicalOrderFulfilledEvent(id, patientId.patientId(), by.userId(), fulfilledAt));
    }

    public boolean isActive() {
        return status == OrderStatus.ACTIVE;
    }

    /** US-25: active and without recorded fulfillment. */
    public boolean isPending() {
        return isActive() && fulfilledAt == null;
    }

    public Long getId() {
        return id;
    }

    public PatientId getPatientId() {
        return patientId;
    }

    public UserId getPrescribedBy() {
        return prescribedBy;
    }

    public Dosage getDosage() {
        return dosage;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getPrescribedAt() {
        return prescribedAt;
    }

    public Long getReplacesOrderId() {
        return replacesOrderId;
    }

    public Long getSupersededBy() {
        return supersededBy;
    }

    public Instant getFulfilledAt() {
        return fulfilledAt;
    }

    public UserId getFulfilledBy() {
        return fulfilledBy;
    }
}
