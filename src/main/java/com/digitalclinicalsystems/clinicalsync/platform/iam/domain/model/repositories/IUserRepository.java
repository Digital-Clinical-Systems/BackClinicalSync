package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.repositories;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.aggregates.User;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.valueobjects.Username;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.repositories.ICrudService;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;

import java.util.List;
import java.util.Optional;

public interface IUserRepository extends ICrudService<User, Long> {
    Optional<User> findByUsername(Username username);
    boolean existsByUsername(Username username);
    List<User> findAllByRole(Roles role);
}
