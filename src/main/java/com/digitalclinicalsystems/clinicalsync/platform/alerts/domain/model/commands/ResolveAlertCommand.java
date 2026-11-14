package com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.commands;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;

public record ResolveAlertCommand(Long alertId, UserId resolvedBy) {
}
