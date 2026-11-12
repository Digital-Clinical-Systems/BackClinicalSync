package com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.commands;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;

import java.util.Set;

public record FulfillMedicalOrderCommand(Long orderId, UserId fulfilledBy, Set<Roles> fulfillerRoles) {
}
