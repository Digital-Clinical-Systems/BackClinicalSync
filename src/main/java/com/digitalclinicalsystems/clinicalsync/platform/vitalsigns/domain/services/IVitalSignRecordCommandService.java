package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.services;

import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.aggregates.VitalSignRecord;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.commands.RecordVitalSignsCommand;

import java.util.Optional;

public interface IVitalSignRecordCommandService {
    Optional<VitalSignRecord> handle(RecordVitalSignsCommand command);
}
