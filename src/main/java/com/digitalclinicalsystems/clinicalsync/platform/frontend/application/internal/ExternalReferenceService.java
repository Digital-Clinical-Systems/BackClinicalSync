package com.digitalclinicalsystems.clinicalsync.platform.frontend.application.internal;

import com.digitalclinicalsystems.clinicalsync.platform.frontend.domain.model.valueobjects.ReferenceType;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.infrastructure.persistence.jpa.entities.ExternalReferencePersistenceEntity;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.infrastructure.persistence.jpa.repositories.ExternalReferencePersistenceRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Translates identifiers between the frontend and the backend.
 * <p>
 * The frontend creates each record with its own identifier and keeps using it
 * for later actions on the same record (acknowledging a handover, fulfilling an
 * order). The backend assigns a numeric id, so the pair is stored here. Records
 * created by the backend itself (seeded patients, alerts) are exposed with their
 * numeric id as text and need no entry.
 */
@Service
public class ExternalReferenceService {

    private final ExternalReferencePersistenceRepository repository;

    public ExternalReferenceService(ExternalReferencePersistenceRepository repository) {
        this.repository = repository;
    }

    /** Backend id resolved from the identifier the frontend sent, if it is known. */
    public Optional<Long> toInternal(ReferenceType type, String externalId) {
        if (externalId == null || externalId.isBlank()) {
            return Optional.empty();
        }
        var stored = repository.findByReferenceTypeAndExternalId(type, externalId)
                .map(ExternalReferencePersistenceEntity::getInternalId);
        if (stored.isPresent()) {
            return stored;
        }
        return externalId.chars().allMatch(Character::isDigit) ? Optional.of(Long.valueOf(externalId)) : Optional.empty();
    }

    /** Whether the frontend identifier was already registered for a created record. */
    public boolean isRegistered(ReferenceType type, String externalId) {
        return externalId != null && repository.findByReferenceTypeAndExternalId(type, externalId).isPresent();
    }

    public void register(ReferenceType type, String externalId, Long internalId) {
        if (externalId == null || externalId.isBlank() || isRegistered(type, externalId)) {
            return;
        }
        repository.save(new ExternalReferencePersistenceEntity(type, externalId, internalId));
    }

    /** Snapshot of every translation of the given type, keyed by backend id. */
    public Translation translation(ReferenceType type) {
        var byInternalId = new HashMap<Long, String>();
        repository.findAllByReferenceType(type).forEach(reference ->
                byInternalId.put(reference.getInternalId(), reference.getExternalId()));
        return new Translation(byInternalId);
    }

    /** Backend id to frontend id for one reference type. */
    public record Translation(Map<Long, String> byInternalId) {
        public String toExternal(Long internalId) {
            if (internalId == null) {
                return null;
            }
            return byInternalId.getOrDefault(internalId, String.valueOf(internalId));
        }
    }
}
