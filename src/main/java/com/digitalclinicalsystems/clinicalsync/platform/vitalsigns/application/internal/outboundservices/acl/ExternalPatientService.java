package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.application.internal.outboundservices.acl;

import com.digitalclinicalsystems.clinicalsync.platform.patients.interfaces.acl.PatientsContextFacade;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.BusinessRuleViolationException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import org.springframework.stereotype.Service;

/**
 * Outbound ACL of BC-03 towards BC-02: a record must reference an existing,
 * currently admitted patient.
 */
@Service
public class ExternalPatientService {

    private final PatientsContextFacade patientsContextFacade;

    public ExternalPatientService(PatientsContextFacade patientsContextFacade) {
        this.patientsContextFacade = patientsContextFacade;
    }

    public void ensurePatientExists(PatientId patientId) {
        if (!patientsContextFacade.existsPatientById(patientId.patientId())) {
            throw new ResourceNotFoundException("Patient", patientId.patientId());
        }
    }

    public void ensurePatientIsAdmitted(PatientId patientId) {
        ensurePatientExists(patientId);
        if (!patientsContextFacade.isPatientAdmitted(patientId.patientId())) {
            throw new BusinessRuleViolationException(
                    "Patient %d has been discharged and no longer accepts clinical records".formatted(patientId.patientId()));
        }
    }
}
