package com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.repositories;

import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.aggregates.Handover;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.valueobjects.HandoverStatus;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.repositories.ICrudService;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;

import java.util.List;

public interface IHandoverRepository extends ICrudService<Handover, Long> {
    List<Handover> findAllByFilters(PatientId patientId, UserId incomingNurseId, HandoverStatus status);
}
