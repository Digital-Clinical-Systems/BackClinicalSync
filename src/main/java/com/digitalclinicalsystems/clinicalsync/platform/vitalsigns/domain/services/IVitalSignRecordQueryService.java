package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.services;

import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.aggregates.VitalSignRecord;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.queries.GetVitalSignRecordByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.queries.GetVitalSignRecordsQuery;

import java.util.List;
import java.util.Optional;

public interface IVitalSignRecordQueryService {
    List<VitalSignRecord> handle(GetVitalSignRecordsQuery query);
    Optional<VitalSignRecord> handle(GetVitalSignRecordByIdQuery query);
}
