package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.repositories;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.aggregates.VitalSignRecord;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Append-only port: a vital sign record is immutable, so there is no update or delete.
 */
public interface IVitalSignRecordRepository {
    VitalSignRecord save(VitalSignRecord record);
    Optional<VitalSignRecord> findById(Long id);
    List<VitalSignRecord> findAllByFilters(PatientId patientId, Instant from, Instant to);
}
