package com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.services;

import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.aggregates.MedicalOrder;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.queries.GetMedicalOrderByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.queries.GetMedicalOrdersQuery;

import java.util.List;
import java.util.Optional;

public interface IMedicalOrderQueryService {
    List<MedicalOrder> handle(GetMedicalOrdersQuery query);
    Optional<MedicalOrder> handle(GetMedicalOrderByIdQuery query);
}
