package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.aggregates;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.commands.RecordVitalSignsCommand;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.events.ClinicalRiskLevelEvaluatedEvent;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.events.VitalSignsRecordedEvent;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.valueobjects.BloodPressure;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.valueobjects.RiskLevel;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VitalSignRecordTest {

    private static RecordVitalSignsCommand command(int systolic, int diastolic, int heartRate, int saturation, double temperature) {
        return new RecordVitalSignsCommand(new PatientId(1L), new UserId(10L), systolic, diastolic, heartRate, saturation, temperature, null);
    }

    @Test
    void derivesCriticalRiskAndNeverAcceptsItFromOutside() {
        var record = new VitalSignRecord(command(168, 94, 126, 89, 38.4));
        assertThat(record.getRiskLevel()).isEqualTo(RiskLevel.CRITICAL);
        assertThat(record.outOfRangeSummary()).contains("oxygen saturation at 89 %");
    }

    @Test
    void rejectsBloodPressureOutOfRange() {
        assertThatThrownBy(() -> new VitalSignRecord(command(300, 90, 80, 97, 36.6)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsSystolicNotGreaterThanDiastolic() {
        assertThatThrownBy(() -> new VitalSignRecord(command(90, 90, 80, 97, 36.6)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Systolic pressure must be greater than diastolic pressure");
    }

    @Test
    void rejectsHeartRateSaturationAndTemperatureOutOfRange() {
        assertThatThrownBy(() -> new VitalSignRecord(command(120, 80, 251, 97, 36.6)))
                .hasMessageContaining("Heart rate");
        assertThatThrownBy(() -> new VitalSignRecord(command(120, 80, 80, 49, 36.6)))
                .hasMessageContaining("Oxygen saturation");
        assertThatThrownBy(() -> new VitalSignRecord(command(120, 80, 80, 97, 43.1)))
                .hasMessageContaining("Temperature");
    }

    @Test
    void registersRecordedAndRiskEvaluatedEventsWithTriggerSource() {
        var record = VitalSignRecord.reconstitute(5L, new PatientId(1L), new UserId(10L), new BloodPressure(120, 80),
                82, 92, 36.6, null, Instant.now());
        record.registerCreationEvents();

        assertThat(record.domainEvents()).hasSize(2);
        assertThat(record.domainEvents()).first().isInstanceOf(VitalSignsRecordedEvent.class);
        var evaluated = (ClinicalRiskLevelEvaluatedEvent) record.domainEvents().stream()
                .filter(ClinicalRiskLevelEvaluatedEvent.class::isInstance).findFirst().orElseThrow();
        assertThat(evaluated.riskLevel()).isEqualTo("WARNING");
        assertThat(evaluated.triggerSource()).isEqualTo("vital-sign:5");
    }

    @Test
    void reconstitutionDoesNotPublishEventsAndDerivesRiskAgain() {
        var record = VitalSignRecord.reconstitute(7L, new PatientId(1L), new UserId(10L), new BloodPressure(168, 94),
                126, 89, 38.4, null, Instant.now());
        assertThat(record.domainEvents()).isEmpty();
        assertThat(record.getRiskLevel()).isEqualTo(RiskLevel.CRITICAL);
    }

    @Test
    void aCorrectionIsANewRecordReferencingThePreviousOne() {
        var correction = new VitalSignRecord(new RecordVitalSignsCommand(
                new PatientId(1L), new UserId(10L), 120, 80, 80, 97, 36.6, 3L));
        assertThat(correction.isCorrection()).isTrue();
        assertThat(correction.getCorrectsRecordId()).isEqualTo(3L);
    }
}
