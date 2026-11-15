package com.digitalclinicalsystems.clinicalsync.platform.iam.application.internal.commandservices;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.commands.SeedRolesCommand;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.entities.Role;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.repositories.IRoleRepository;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services.IRoleCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class RoleCommandServiceImpl implements IRoleCommandService {

    private final IRoleRepository roleRepository;

    public RoleCommandServiceImpl(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void handle(SeedRolesCommand command) {
        Arrays.stream(Roles.values())
                .filter(role -> !roleRepository.existsByName(role))
                .forEach(role -> roleRepository.save(new Role(role)));
    }
}
