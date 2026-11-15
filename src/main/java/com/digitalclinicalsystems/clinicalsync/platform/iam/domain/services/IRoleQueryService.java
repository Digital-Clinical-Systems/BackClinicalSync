package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.entities.Role;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.queries.GetAllRolesQuery;

import java.util.List;

public interface IRoleQueryService {
    List<Role> handle(GetAllRolesQuery query);
}
