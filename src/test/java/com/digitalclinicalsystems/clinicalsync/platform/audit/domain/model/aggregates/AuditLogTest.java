package com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.aggregates;

import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.commands.CreateAuditLogCommand;
import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.valueobjects.ActionType;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuditLogTest {

    @Test
    void derivesActionTypeFromTheDomainEventName() {
        assertThat(ActionType.fromEventName("VitalSignsRecordedEvent").actionType()).isEqualTo("VITAL_SIGNS_RECORDED");
        assertThat(ActionType.fromEventName("SbarHandoverIssuedEvent").actionType()).isEqualTo("SBAR_HANDOVER_ISSUED");
    }

    @Test
    void requiresActorTimestampAndAffectedResource() {
        assertThatThrownBy(() -> new AuditLog(new CreateAuditLogCommand("AlertRaisedEvent", Instant.now(), 1L, "alert:1", " ", null)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new AuditLog(new CreateAuditLogCommand("AlertRaisedEvent", null, 1L, "alert:1", "system", null)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new AuditLog(new CreateAuditLogCommand("AlertRaisedEvent", Instant.now(), 1L, "", "system", null)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void keepsEmptyMetadataAsJsonObject() {
        var auditLog = new AuditLog(new CreateAuditLogCommand("PatientAdmittedEvent", Instant.now(), 1L, "patient:1", "system", null));
        assertThat(auditLog.getActionType().actionType()).isEqualTo("PATIENT_ADMITTED");
        assertThat(auditLog.getMetadata()).isEqualTo("{}");
    }
}
