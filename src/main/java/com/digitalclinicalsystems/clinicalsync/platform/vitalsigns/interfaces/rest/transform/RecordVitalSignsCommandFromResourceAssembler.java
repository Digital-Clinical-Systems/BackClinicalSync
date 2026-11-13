package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.interfaces.rest.transform;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.commands.RecordVitalSignsCommand;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.interfaces.rest.resources.CreateVitalSignRecordResource;

public class RecordVitalSignsCommandFromResourceAssembler {
    public static RecordVitalSignsCommand toCommandFromResource(CreateVitalSignRecordResource resource, UserId recordedBy) {
        return new RecordVitalSignsCommand(
                new PatientId(resource.patientId()),
                recordedBy,
                resource.systolic(),
                resource.diastolic(),
                resource.heartRate(),
                resource.oxygenSaturation(),
                resource.temperature(),
                resource.correctsRecordId());
    }
}
