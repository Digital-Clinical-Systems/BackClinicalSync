package com.digitalclinicalsystems.clinicalsync.platform.frontend.infrastructure.persistence.jpa.repositories;

import com.digitalclinicalsystems.clinicalsync.platform.frontend.domain.model.valueobjects.ReferenceType;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.infrastructure.persistence.jpa.entities.ExternalReferencePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExternalReferencePersistenceRepository extends
        JpaRepository<ExternalReferencePersistenceEntity, Long> {

    List<ExternalReferencePersistenceEntity> findAllByReferenceType(ReferenceType referenceType);

    Optional<ExternalReferencePersistenceEntity> findByReferenceTypeAndExternalId(ReferenceType referenceType, String externalId);
}
