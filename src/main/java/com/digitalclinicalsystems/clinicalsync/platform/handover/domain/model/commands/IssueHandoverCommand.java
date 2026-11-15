package com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.commands;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;

import java.util.Set;

public record IssueHandoverCommand(
        PatientId patientId,
        UserId outgoingNurseId,
        Set<Roles> outgoingNurseRoles,
        UserId incomingNurseId,
        String situation,
        String background,
        String assessment,
        String recommendation
) {
}
