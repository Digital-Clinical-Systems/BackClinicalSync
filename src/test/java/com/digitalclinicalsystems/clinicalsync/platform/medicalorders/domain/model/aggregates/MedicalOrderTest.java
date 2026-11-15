package com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.aggregates;

import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.commands.IssueMedicalOrderCommand;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.valueobjects.Dosage;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.valueobjects.OrderStatus;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ActionNotAllowedException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.BusinessRuleViolationException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MedicalOrderTest {

    private static final UserId PHYSICIAN = new UserId(3L);
    private static final UserId NURSE = new UserId(1L);
    private static final Dosage DOSAGE = new Dosage("Enoxaparin", "40 mg", "Subcutaneous", "Every 12 hours");

    private static MedicalOrder activeOrder(Long id, Long patientId) {
        return MedicalOrder.reconstitute(id, new PatientId(patientId), PHYSICIAN, DOSAGE, OrderStatus.ACTIVE,
                Instant.now(), null, null, null, null);
    }

    @Test
    void onlyAPhysicianIssuesAnOrder() {
        var command = new IssueMedicalOrderCommand(new PatientId(1L), NURSE, Set.of(Roles.NURSE),
                "Aspirin", "100 mg", "Oral", "Every 24 hours", null);
        assertThatThrownBy(() -> new MedicalOrder(command))
                .isInstanceOf(ActionNotAllowedException.class)
                .hasMessage("Only a physician can issue a medical order");
    }

    @Test
    void dosageDataIsMandatory() {
        var command = new IssueMedicalOrderCommand(new PatientId(1L), PHYSICIAN, Set.of(Roles.PHYSICIAN),
                "Aspirin", "", "Oral", " ", null);
        assertThatThrownBy(() -> new MedicalOrder(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("dose, frequency");
    }

    @Test
    void replacementLeavesThePreviousOrderSuperseded() {
        var previous = activeOrder(1L, 1L);
        var replacement = activeOrder(2L, 1L);
        previous.supersede(replacement);

        assertThat(previous.getStatus()).isEqualTo(OrderStatus.SUPERSEDED);
        assertThat(previous.getSupersededBy()).isEqualTo(2L);
        assertThat(previous.isActive()).isFalse();
        assertThatThrownBy(() -> previous.supersede(activeOrder(3L, 1L)))
                .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    void replacementMustBelongToTheSamePatient() {
        assertThatThrownBy(() -> activeOrder(1L, 1L).supersede(activeOrder(2L, 2L)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void fulfillmentIsRecordedByNursingOnlyOnceAndNeverByThePrescriber() {
        var order = activeOrder(1L, 1L);
        assertThatThrownBy(() -> order.fulfill(PHYSICIAN, Set.of(Roles.PHYSICIAN)))
                .isInstanceOf(ActionNotAllowedException.class);
        assertThatThrownBy(() -> order.fulfill(PHYSICIAN, Set.of(Roles.NURSE, Roles.PHYSICIAN)))
                .isInstanceOf(ActionNotAllowedException.class)
                .hasMessage("The prescriber cannot record the fulfillment of their own order");

        order.fulfill(NURSE, Set.of(Roles.NURSE));
        assertThat(order.isPending()).isFalse();
        assertThat(order.getFulfilledBy()).isEqualTo(NURSE);
        assertThatThrownBy(() -> order.fulfill(NURSE, Set.of(Roles.NURSE)))
                .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    void supersededOrderCannotBeFulfilled() {
        var order = activeOrder(1L, 1L);
        order.supersede(activeOrder(2L, 1L));
        assertThatThrownBy(() -> order.fulfill(NURSE, Set.of(Roles.NURSE)))
                .isInstanceOf(BusinessRuleViolationException.class);
    }
}
