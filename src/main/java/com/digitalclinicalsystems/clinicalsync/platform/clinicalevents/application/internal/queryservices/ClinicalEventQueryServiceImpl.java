package com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.application.internal.queryservices;

import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.aggregates.ClinicalEvent;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.queries.GetClinicalEventByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.queries.GetClinicalEventsQuery;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.repositories.IClinicalEventRepository;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.services.IClinicalEventQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClinicalEventQueryServiceImpl implements IClinicalEventQueryService {

    private final IClinicalEventRepository clinicalEventRepository;

    public ClinicalEventQueryServiceImpl(IClinicalEventRepository clinicalEventRepository) {
        this.clinicalEventRepository = clinicalEventRepository;
    }

    @Override
    public List<ClinicalEvent> handle(GetClinicalEventsQuery query) {
        var patientId = query.patientId() == null ? null : new PatientId(query.patientId());
        return clinicalEventRepository.findAllByFilters(patientId, query.type(), query.from(), query.to());
    }

    @Override
    public Optional<ClinicalEvent> handle(GetClinicalEventByIdQuery query) {
        return clinicalEventRepository.findById(query.clinicalEventId());
    }
}
