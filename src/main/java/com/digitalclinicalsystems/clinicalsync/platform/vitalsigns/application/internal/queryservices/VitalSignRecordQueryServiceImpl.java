package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.application.internal.queryservices;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.aggregates.VitalSignRecord;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.queries.GetVitalSignRecordByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.queries.GetVitalSignRecordsQuery;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.repositories.IVitalSignRecordRepository;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.services.IVitalSignRecordQueryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VitalSignRecordQueryServiceImpl implements IVitalSignRecordQueryService {

    private final IVitalSignRecordRepository vitalSignRecordRepository;

    public VitalSignRecordQueryServiceImpl(IVitalSignRecordRepository vitalSignRecordRepository) {
        this.vitalSignRecordRepository = vitalSignRecordRepository;
    }

    @Override
    public List<VitalSignRecord> handle(GetVitalSignRecordsQuery query) {
        var patientId = query.patientId() == null ? null : new PatientId(query.patientId());
        return vitalSignRecordRepository.findAllByFilters(patientId, query.from(), query.to());
    }

    @Override
    public Optional<VitalSignRecord> handle(GetVitalSignRecordByIdQuery query) {
        return vitalSignRecordRepository.findById(query.recordId());
    }
}
