package com.digitalclinicalsystems.clinicalsync.platform.medicalorders.interfaces.rest.transform;

import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.aggregates.MedicalOrder;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.interfaces.rest.resources.MedicalOrderResource;

public class MedicalOrderResourceFromEntityAssembler {
    public static MedicalOrderResource toResourceFromEntity(MedicalOrder order) {
        return new MedicalOrderResource(
                order.getId(),
                order.getPatientId().patientId(),
                order.getPrescribedBy().userId(),
                order.getDosage().medication(),
                order.getDosage().dose(),
                order.getDosage().route(),
                order.getDosage().frequency(),
                order.getStatus().name(),
                order.getPrescribedAt(),
                order.getReplacesOrderId(),
                order.getSupersededBy(),
                order.getFulfilledAt(),
                order.getFulfilledBy() == null ? null : order.getFulfilledBy().userId());
    }
}
