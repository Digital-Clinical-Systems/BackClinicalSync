package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.aggregates.User;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.commands.SignInCommand;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.commands.SignUpCommand;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.valueobjects.AuthenticatedUser;

import java.util.Optional;

public interface IUserCommandService {
    Optional<User> handle(SignUpCommand command);
    Optional<AuthenticatedUser> handle(SignInCommand command);
}
