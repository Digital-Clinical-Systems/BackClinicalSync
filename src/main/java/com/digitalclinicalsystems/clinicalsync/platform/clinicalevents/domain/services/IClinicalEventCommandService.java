package com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.services;

import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.aggregates.ClinicalEvent;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.commands.RecordClinicalEventCommand;

import java.util.Optional;

public interface IClinicalEventCommandService {
    Optional<ClinicalEvent> handle(RecordClinicalEventCommand command);
}
