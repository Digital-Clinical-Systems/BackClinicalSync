package com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.services;

import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.aggregates.MedicalOrder;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.commands.FulfillMedicalOrderCommand;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.commands.IssueMedicalOrderCommand;

import java.util.Optional;

public interface IMedicalOrderCommandService {
    Optional<MedicalOrder> handle(IssueMedicalOrderCommand command);
    Optional<MedicalOrder> handle(FulfillMedicalOrderCommand command);
}
