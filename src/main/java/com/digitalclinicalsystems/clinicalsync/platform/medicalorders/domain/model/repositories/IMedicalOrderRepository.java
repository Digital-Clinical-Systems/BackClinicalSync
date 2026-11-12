package com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.repositories;

import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.aggregates.MedicalOrder;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.valueobjects.OrderStatus;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.repositories.ICrudService;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;

import java.util.List;

public interface IMedicalOrderRepository extends ICrudService<MedicalOrder, Long> {
    List<MedicalOrder> findAllByFilters(PatientId patientId, OrderStatus status);
}
