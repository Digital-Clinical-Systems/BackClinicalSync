package com.digitalclinicalsystems.clinicalsync.platform.iam.application.internal.commandservices;

import com.digitalclinicalsystems.clinicalsync.platform.iam.application.internal.outboundservices.hashing.IHashingService;
import com.digitalclinicalsystems.clinicalsync.platform.iam.application.internal.outboundservices.tokens.ITokenService;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.aggregates.User;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.commands.SignInCommand;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.commands.SignUpCommand;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.repositories.IRoleRepository;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.repositories.IUserRepository;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.valueobjects.AuthenticatedUser;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services.IUserCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.BusinessRuleViolationException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserCommandServiceImpl implements IUserCommandService {

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final IHashingService hashingService;
    private final ITokenService tokenService;

    public UserCommandServiceImpl(IUserRepository userRepository, IRoleRepository roleRepository,
                                  IHashingService hashingService, ITokenService tokenService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
    }

    @Override
    public Optional<AuthenticatedUser> handle(SignInCommand command) {
        var user = userRepository.findByUsername(command.username())
                .filter(candidate -> hashingService.matches(command.password(), candidate.getPassword()))
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));
        var token = tokenService.generateToken(user.getUsername());
        return Optional.of(new AuthenticatedUser(user, token));
    }

    @Override
    public Optional<User> handle(SignUpCommand command) {
        if (userRepository.existsByUsername(command.username())) {
            throw new BusinessRuleViolationException("Username %s is already taken".formatted(command.username()));
        }
        if (command.roles() == null || command.roles().isEmpty()) {
            throw new IllegalArgumentException("A user must have at least one role");
        }
        var roles = command.roles().stream()
                .map(name -> roleRepository.findByName(name)
                        .orElseThrow(() -> new ResourceNotFoundException("Role %s not found".formatted(name))))
                .toList();
        var user = new User(
                command.username(),
                hashingService.encode(command.password()),
                command.fullName(),
                roles,
                command.shift(),
                command.specialty());
        return Optional.of(userRepository.save(user));
    }
}
