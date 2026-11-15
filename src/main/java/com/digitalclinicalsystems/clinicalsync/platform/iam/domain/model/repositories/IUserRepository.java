package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.repositories;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.aggregates.User;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.repositories.ICrudService;

import java.util.Optional;

public interface IUserRepository extends ICrudService<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
