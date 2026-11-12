package com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.queries;

import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.valueobjects.OrderStatus;

/**
 * @param pending {@code true} returns active orders without fulfillment (US-25)
 */
public record GetMedicalOrdersQuery(Long patientId, OrderStatus status, Boolean pending) {
}
