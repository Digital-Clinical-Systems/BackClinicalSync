package com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.repositories;

import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.aggregates.ClinicalEvent;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.valueobjects.ClinicalEventType;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Append-only port, like the audit log: annotations are never edited nor deleted.
 */
public interface IClinicalEventRepository {
    ClinicalEvent save(ClinicalEvent clinicalEvent);
    Optional<ClinicalEvent> findById(Long id);
    List<ClinicalEvent> findAllByFilters(PatientId patientId, ClinicalEventType type, Instant from, Instant to);
}
