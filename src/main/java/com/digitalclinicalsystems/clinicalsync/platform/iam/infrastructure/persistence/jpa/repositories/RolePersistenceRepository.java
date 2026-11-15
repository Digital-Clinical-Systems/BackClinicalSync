package com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.persistence.jpa.repositories;

import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.persistence.jpa.entities.RolePersistenceEntity;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolePersistenceRepository extends
        JpaRepository<RolePersistenceEntity, Long> {
    Optional<RolePersistenceEntity> findByName(Roles name);
    boolean existsByName(Roles name);
}
