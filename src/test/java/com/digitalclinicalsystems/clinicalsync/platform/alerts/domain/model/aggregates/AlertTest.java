package com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.aggregates;

import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.commands.RaiseAlertCommand;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.AlertSeverity;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.AlertStatus;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.BusinessRuleViolationException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AlertTest {

    private static final UserId NURSE = new UserId(1L);

    private static Alert openAlert() {
        return new Alert(new RaiseAlertCommand(new PatientId(1L), AlertSeverity.CRITICAL, "vital-sign:5", "SpO2 89 %"));
    }

    @Test
    void requiresTriggerSource() {
        assertThatThrownBy(() -> new Alert(new RaiseAlertCommand(new PatientId(1L), AlertSeverity.WARNING, "  ", "x")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("An alert cannot exist without the source that triggered it");
    }

    @Test
    void cannotBeResolvedWithoutBeingAcknowledged() {
        var alert = openAlert();
        assertThatThrownBy(() -> alert.resolve(NURSE))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("An alert cannot be resolved before being acknowledged");
        assertThat(alert.getStatus()).isEqualTo(AlertStatus.OPEN);
    }

    @Test
    void followsOpenAcknowledgedResolvedLifecycle() {
        var alert = openAlert();
        alert.acknowledge(NURSE);
        assertThat(alert.getStatus()).isEqualTo(AlertStatus.ACKNOWLEDGED);
        assertThat(alert.getAcknowledgedBy()).isEqualTo(NURSE);
        assertThat(alert.getAcknowledgedAt()).isNotNull();
        alert.resolve(NURSE);
        assertThat(alert.getStatus()).isEqualTo(AlertStatus.RESOLVED);
        assertThat(alert.domainEvents()).hasSize(2);
    }

    @Test
    void acknowledgingRequiresTheResponsibleUser() {
        assertThatThrownBy(() -> openAlert().acknowledge(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void resolvedAlertNeverGoesBack() {
        var alert = openAlert();
        alert.acknowledge(NURSE);
        alert.resolve(NURSE);
        assertThatThrownBy(() -> alert.acknowledge(NURSE)).isInstanceOf(BusinessRuleViolationException.class);
        assertThatThrownBy(() -> alert.resolve(NURSE)).isInstanceOf(BusinessRuleViolationException.class);
        assertThat(alert.getStatus()).isEqualTo(AlertStatus.RESOLVED);
    }

    @Test
    void cannotBeAcknowledgedTwice() {
        var alert = openAlert();
        alert.acknowledge(NURSE);
        assertThatThrownBy(() -> alert.acknowledge(NURSE)).isInstanceOf(BusinessRuleViolationException.class);
    }
}
