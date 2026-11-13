package com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.queries;

import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.valueobjects.HandoverStatus;

/**
 * @param incomingNurseId inbox of the incoming nurse (US-14)
 */
public record GetHandoversQuery(Long patientId, Long incomingNurseId, HandoverStatus status) {
}
