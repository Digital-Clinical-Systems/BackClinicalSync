package com.digitalclinicalsystems.clinicalsync.platform.iam.application.internal.queryservices;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.entities.Role;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.queries.GetAllRolesQuery;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.repositories.IRoleRepository;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services.IRoleQueryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleQueryServiceImpl implements IRoleQueryService {

    private final IRoleRepository roleRepository;

    public RoleQueryServiceImpl(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> handle(GetAllRolesQuery query) {
        return roleRepository.findAll();
    }
}
