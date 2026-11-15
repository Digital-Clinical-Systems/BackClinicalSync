package com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.aggregates;

import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.commands.RecordClinicalEventCommand;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.events.CriticalClinicalEventRecordedEvent;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.valueobjects.ClinicalEventSeverity;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.valueobjects.ClinicalEventType;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ClinicalEventTest {

    @Test
    void medicationAdministrationRequiresMedicationAndDose() {
        var command = new RecordClinicalEventCommand(new PatientId(1L), new UserId(10L),
                ClinicalEventType.MEDICATION_ADMINISTRATION, ClinicalEventSeverity.ROUTINE, "Morning dose", " ", null, null);
        assertThatThrownBy(() -> new ClinicalEvent(command))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("medication, dose");
    }

    @Test
    void descriptionIsMandatory() {
        var command = new RecordClinicalEventCommand(new PatientId(1L), new UserId(10L),
                ClinicalEventType.CLINICAL_OBSERVATION, ClinicalEventSeverity.NOTABLE, "  ", null, null, null);
        assertThatThrownBy(() -> new ClinicalEvent(command)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void criticalEventAsksForAnAlertWithClinicalEventTriggerSource() {
        var event = ClinicalEvent.reconstitute(4L, new PatientId(1L), new UserId(10L), ClinicalEventType.CLINICAL_OBSERVATION,
                ClinicalEventSeverity.CRITICAL, "Cardiac arrest", null, null, null, Instant.now());
        event.registerCreationEvents();
        var critical = event.domainEvents().stream()
                .filter(CriticalClinicalEventRecordedEvent.class::isInstance)
                .map(CriticalClinicalEventRecordedEvent.class::cast)
                .findFirst().orElseThrow();
        assertThat(critical.triggerSource()).isEqualTo("clinical-event:4");
    }
}
