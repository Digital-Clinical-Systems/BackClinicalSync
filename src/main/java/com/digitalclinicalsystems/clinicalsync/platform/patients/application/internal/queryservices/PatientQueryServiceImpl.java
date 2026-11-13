package com.digitalclinicalsystems.clinicalsync.platform.patients.application.internal.queryservices;

import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.aggregates.Patient;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.queries.GetAllPatientsQuery;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.queries.GetPatientByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.queries.GetPatientByMedicalRecordNumberQuery;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.repositories.IPatientRepository;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.valueobjects.MedicalRecordNumber;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.services.IPatientQueryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientQueryServiceImpl implements IPatientQueryService {

    private final IPatientRepository patientRepository;

    public PatientQueryServiceImpl(IPatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public List<Patient> handle(GetAllPatientsQuery query) {
        return query.active() == null
                ? patientRepository.findAll()
                : patientRepository.findAllByActive(query.active());
    }

    @Override
    public Optional<Patient> handle(GetPatientByIdQuery query) {
        return patientRepository.findById(query.patientId());
    }

    @Override
    public Optional<Patient> handle(GetPatientByMedicalRecordNumberQuery query) {
        return patientRepository.findByMedicalRecordNumber(new MedicalRecordNumber(query.medicalRecordNumber()));
    }
}
