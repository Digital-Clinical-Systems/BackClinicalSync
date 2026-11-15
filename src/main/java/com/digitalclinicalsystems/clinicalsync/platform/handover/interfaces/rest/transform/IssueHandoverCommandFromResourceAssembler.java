package com.digitalclinicalsystems.clinicalsync.platform.handover.interfaces.rest.transform;

import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.commands.IssueHandoverCommand;
import com.digitalclinicalsystems.clinicalsync.platform.handover.interfaces.rest.resources.CreateHandoverResource;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;

import java.util.Set;

public class IssueHandoverCommandFromResourceAssembler {
    public static IssueHandoverCommand toCommandFromResource(CreateHandoverResource resource, UserId outgoingNurseId,
                                                             Set<Roles> outgoingNurseRoles) {
        return new IssueHandoverCommand(
                new PatientId(resource.patientId()),
                outgoingNurseId,
                outgoingNurseRoles,
                new UserId(resource.incomingNurseId()),
                resource.situation(),
                resource.background(),
                resource.assessment(),
                resource.recommendation());
    }
}
