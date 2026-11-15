package com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.interfaces.rest.transform;

import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.aggregates.ClinicalEvent;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.interfaces.rest.resources.ClinicalEventResource;

public class ClinicalEventResourceFromEntityAssembler {
    public static ClinicalEventResource toResourceFromEntity(ClinicalEvent clinicalEvent) {
        return new ClinicalEventResource(
                clinicalEvent.getId(),
                clinicalEvent.getPatientId().patientId(),
                clinicalEvent.getRecordedBy().userId(),
                clinicalEvent.getType().name(),
                clinicalEvent.getSeverity().name(),
                clinicalEvent.getDescription(),
                clinicalEvent.getMedication(),
                clinicalEvent.getDose(),
                clinicalEvent.getRelatedOrderId(),
                clinicalEvent.getOccurredAt());
    }
}
