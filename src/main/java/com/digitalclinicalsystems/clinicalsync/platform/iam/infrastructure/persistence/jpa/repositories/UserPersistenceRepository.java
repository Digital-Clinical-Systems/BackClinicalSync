package com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.persistence.jpa.repositories;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.valueobjects.Username;
import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPersistenceRepository extends JpaRepository<UserPersistenceEntity, Long> {

    Optional<UserPersistenceEntity> findByUsername(Username username);

    boolean existsByUsername(Username username);

    /** Explicit join over the role collection: a derived query cannot express it. */
    @Query("select u from UserPersistenceEntity u join u.roles r where r = :role order by u.id")
    List<UserPersistenceEntity> findAllByRole(@Param("role") Roles role);
}
