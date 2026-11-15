package com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.services;

import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.aggregates.ClinicalEvent;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.queries.GetClinicalEventByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.queries.GetClinicalEventsQuery;

import java.util.List;
import java.util.Optional;

public interface IClinicalEventQueryService {
    List<ClinicalEvent> handle(GetClinicalEventsQuery query);
    Optional<ClinicalEvent> handle(GetClinicalEventByIdQuery query);
}
