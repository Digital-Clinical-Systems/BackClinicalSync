package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.commands;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;

/**
 * @param correctsRecordId record this one corrects; a correction is a new record, never an edit
 */
public record RecordVitalSignsCommand(
        PatientId patientId,
        UserId recordedBy,
        Integer systolic,
        Integer diastolic,
        Integer heartRate,
        Integer oxygenSaturation,
        Double temperature,
        Long correctsRecordId
) {
}
