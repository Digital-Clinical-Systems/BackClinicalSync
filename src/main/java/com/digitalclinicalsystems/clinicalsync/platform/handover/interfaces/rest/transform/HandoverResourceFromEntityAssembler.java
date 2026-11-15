package com.digitalclinicalsystems.clinicalsync.platform.handover.interfaces.rest.transform;

import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.aggregates.Handover;
import com.digitalclinicalsystems.clinicalsync.platform.handover.interfaces.rest.resources.HandoverResource;

public class HandoverResourceFromEntityAssembler {
    public static HandoverResource toResourceFromEntity(Handover handover) {
        return new HandoverResource(
                handover.getId(),
                handover.getPatientId().patientId(),
                handover.getOutgoingNurseId().userId(),
                handover.getIncomingNurseId().userId(),
                handover.getContent().situation(),
                handover.getContent().background(),
                handover.getContent().assessment(),
                handover.getContent().recommendation(),
                handover.getStatus().name(),
                handover.getIssuedAt(),
                handover.getAcknowledgedAt());
    }
}
