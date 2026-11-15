package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.commands.SeedRolesCommand;

public interface IRoleCommandService {
    void handle(SeedRolesCommand command);
}
