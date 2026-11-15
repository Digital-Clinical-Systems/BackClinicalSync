package com.digitalclinicalsystems.clinicalsync.platform.handover.domain.services;

import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.aggregates.Handover;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.queries.GetHandoverByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.queries.GetHandoversQuery;

import java.util.List;
import java.util.Optional;

public interface IHandoverQueryService {
    List<Handover> handle(GetHandoversQuery query);
    Optional<Handover> handle(GetHandoverByIdQuery query);
}
