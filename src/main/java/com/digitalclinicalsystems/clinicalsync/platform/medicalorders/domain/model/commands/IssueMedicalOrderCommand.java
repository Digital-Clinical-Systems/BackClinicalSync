package com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.commands;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;

import java.util.Set;

/**
 * @param replacesOrderId active order of the same patient that this one replaces, if any
 */
public record IssueMedicalOrderCommand(
        PatientId patientId,
        UserId prescribedBy,
        Set<Roles> prescriberRoles,
        String medication,
        String dose,
        String route,
        String frequency,
        Long replacesOrderId
) {
}
