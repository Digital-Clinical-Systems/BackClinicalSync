package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.repositories;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.entities.Role;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.repositories.ICrudService;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;

import java.util.Optional;

public interface IRoleRepository extends ICrudService<Role, Long> {
    Optional<Role> findByName(Roles name);
    boolean existsByName(Roles name);
}
