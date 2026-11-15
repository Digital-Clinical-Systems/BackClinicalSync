package com.digitalclinicalsystems.clinicalsync.platform.medicalorders.interfaces.rest.transform;

import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.commands.IssueMedicalOrderCommand;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.interfaces.rest.resources.CreateMedicalOrderResource;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;

import java.util.Set;

public class IssueMedicalOrderCommandFromResourceAssembler {
    public static IssueMedicalOrderCommand toCommandFromResource(CreateMedicalOrderResource resource, UserId prescribedBy,
                                                                 Set<Roles> prescriberRoles) {
        return new IssueMedicalOrderCommand(
                new PatientId(resource.patientId()),
                prescribedBy,
                prescriberRoles,
                resource.medication(),
                resource.dose(),
                resource.route(),
                resource.frequency(),
                resource.replacesOrderId());
    }
}
