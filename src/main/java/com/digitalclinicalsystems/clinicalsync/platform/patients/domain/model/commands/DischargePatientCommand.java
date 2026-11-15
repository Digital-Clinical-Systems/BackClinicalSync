package com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.commands;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;

public record DischargePatientCommand(Long patientId, UserId dischargedBy) {
}
