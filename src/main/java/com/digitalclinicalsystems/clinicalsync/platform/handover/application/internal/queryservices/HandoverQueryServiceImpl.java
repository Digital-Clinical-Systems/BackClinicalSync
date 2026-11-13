package com.digitalclinicalsystems.clinicalsync.platform.handover.application.internal.queryservices;

import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.aggregates.Handover;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.queries.GetHandoverByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.queries.GetHandoversQuery;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.repositories.IHandoverRepository;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.services.IHandoverQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HandoverQueryServiceImpl implements IHandoverQueryService {

    private final IHandoverRepository handoverRepository;

    public HandoverQueryServiceImpl(IHandoverRepository handoverRepository) {
        this.handoverRepository = handoverRepository;
    }

    @Override
    public List<Handover> handle(GetHandoversQuery query) {
        var patientId = query.patientId() == null ? null : new PatientId(query.patientId());
        var incomingNurseId = query.incomingNurseId() == null ? null : new UserId(query.incomingNurseId());
        return handoverRepository.findAllByFilters(patientId, incomingNurseId, query.status());
    }

    @Override
    public Optional<Handover> handle(GetHandoverByIdQuery query) {
        return handoverRepository.findById(query.handoverId());
    }
}
