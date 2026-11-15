package com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.commands;

import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.valueobjects.ClinicalEventSeverity;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.valueobjects.ClinicalEventType;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;

public record RecordClinicalEventCommand(
        PatientId patientId,
        UserId recordedBy,
        ClinicalEventType type,
        ClinicalEventSeverity severity,
        String description,
        String medication,
        String dose,
        Long relatedOrderId
) {
}
