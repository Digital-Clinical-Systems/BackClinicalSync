package com.digitalclinicalsystems.clinicalsync.platform.patients.interfaces.acl;

import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.queries.GetPatientByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.services.IPatientQueryService;
import org.springframework.stereotype.Service;

/**
 * Anti-corruption layer of BC-02. Other contexts only learn whether a patient
 * exists and is currently admitted; they never read or write patient data.
 */
@Service
public class PatientsContextFacade {

    private final IPatientQueryService patientQueryService;

    public PatientsContextFacade(IPatientQueryService patientQueryService) {
        this.patientQueryService = patientQueryService;
    }

    public boolean existsPatientById(Long patientId) {
        return patientQueryService.handle(new GetPatientByIdQuery(patientId)).isPresent();
    }

    public boolean isPatientAdmitted(Long patientId) {
        return patientQueryService.handle(new GetPatientByIdQuery(patientId))
                .map(patient -> patient.isActive())
                .orElse(false);
    }
}
