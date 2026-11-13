package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.aggregates.User;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.queries.GetAllRolesQuery;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.queries.GetAllUsersQuery;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.queries.GetUserByUsernameQuery;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;

import java.util.List;
import java.util.Optional;

public interface IUserQueryService {
    List<User> handle(GetAllUsersQuery query);
    Optional<User> handle(GetUserByIdQuery query);
    Optional<User> handle(GetUserByUsernameQuery query);
    List<Roles> handle(GetAllRolesQuery query);
}
