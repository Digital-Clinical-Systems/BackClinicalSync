package com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.commands;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;

public record AcknowledgeHandoverCommand(Long handoverId, UserId acknowledgedBy) {
}
