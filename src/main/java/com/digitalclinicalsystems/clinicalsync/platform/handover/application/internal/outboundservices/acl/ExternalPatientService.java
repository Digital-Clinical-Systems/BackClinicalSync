package com.digitalclinicalsystems.clinicalsync.platform.handover.application.internal.outboundservices.acl;

import com.digitalclinicalsystems.clinicalsync.platform.patients.interfaces.acl.PatientsContextFacade;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.BusinessRuleViolationException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import org.springframework.stereotype.Service;

/**
 * Outbound ACL of BC-05 towards BC-02.
 */
@Service
public class ExternalPatientService {

    private final PatientsContextFacade patientsContextFacade;

    public ExternalPatientService(PatientsContextFacade patientsContextFacade) {
        this.patientsContextFacade = patientsContextFacade;
    }

    public void ensurePatientIsAdmitted(PatientId patientId) {
        if (!patientsContextFacade.existsPatientById(patientId.patientId())) {
            throw new ResourceNotFoundException("Patient", patientId.patientId());
        }
        if (!patientsContextFacade.isPatientAdmitted(patientId.patientId())) {
            throw new BusinessRuleViolationException(
                    "Patient %d has been discharged and no longer accepts handovers".formatted(patientId.patientId()));
        }
    }
}
