package com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.interfaces.rest.transform;

import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.commands.RecordClinicalEventCommand;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.valueobjects.ClinicalEventSeverity;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.valueobjects.ClinicalEventType;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.interfaces.rest.resources.CreateClinicalEventResource;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;

public class RecordClinicalEventCommandFromResourceAssembler {
    public static RecordClinicalEventCommand toCommandFromResource(CreateClinicalEventResource resource, UserId recordedBy) {
        return new RecordClinicalEventCommand(
                new PatientId(resource.patientId()),
                recordedBy,
                parse(ClinicalEventType.class, resource.type(), "type"),
                parse(ClinicalEventSeverity.class, resource.severity(), "severity"),
                resource.description(),
                resource.medication(),
                resource.dose(),
                resource.relatedOrderId());
    }

    private static <E extends Enum<E>> E parse(Class<E> type, String value, String field) {
        try {
            return Enum.valueOf(type, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid %s: %s".formatted(field, value), e);
        }
    }
}
