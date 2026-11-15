package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.interfaces.rest.transform;

import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.aggregates.VitalSignRecord;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.interfaces.rest.resources.VitalSignRecordResource;

public class VitalSignRecordResourceFromEntityAssembler {
    public static VitalSignRecordResource toResourceFromEntity(VitalSignRecord record) {
        return new VitalSignRecordResource(
                record.getId(),
                record.getPatientId().patientId(),
                record.getRecordedBy().userId(),
                record.getBloodPressure().systolic(),
                record.getBloodPressure().diastolic(),
                record.getHeartRate(),
                record.getOxygenSaturation(),
                record.getTemperature(),
                record.getBloodPressure().meanArterialPressure(),
                record.getRiskLevel().name(),
                record.getMeasuredAt(),
                record.getCorrectsRecordId());
    }
}
