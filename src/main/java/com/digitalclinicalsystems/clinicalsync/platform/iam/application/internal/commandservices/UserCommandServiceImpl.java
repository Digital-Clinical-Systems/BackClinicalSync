package com.digitalclinicalsystems.clinicalsync.platform.iam.application.internal.commandservices;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.aggregates.User;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.commands.SignInCommand;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.commands.SignUpCommand;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.repositories.IUserRepository;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.valueobjects.AuthenticatedUser;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.valueobjects.HashedPassword;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.valueobjects.Username;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services.IHashingService;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services.ITokenService;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services.IUserCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.BusinessRuleViolationException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserCommandServiceImpl implements IUserCommandService {

    /**
     * The same message for an unknown username and for a wrong password: telling
     * them apart would let anyone probe which professionals exist in the service.
     */
    private static final String INVALID_CREDENTIALS = "Invalid username or password";

    private final IUserRepository userRepository;
    private final IHashingService hashingService;
    private final ITokenService tokenService;
    private final ApplicationEventPublisher eventPublisher;

    public UserCommandServiceImpl(IUserRepository userRepository,
                                  IHashingService hashingService,
                                  ITokenService tokenService,
                                  ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Optional<User> handle(SignUpCommand command) {
        var username = new Username(command.username());
        if (userRepository.existsByUsername(username)) {
            throw new BusinessRuleViolationException(
                    "A user with username %s already exists".formatted(username.username()));
        }
        if (command.password() == null || command.password().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        var user = new User(username, command.fullName(),
                new HashedPassword(hashingService.encode(command.password())), command.roles());
        var saved = userRepository.save(user);
        saved.registerCreationEvents();
        publishDomainEvents(saved);
        return Optional.of(saved);
    }

    @Override
    public Optional<AuthenticatedUser> handle(SignInCommand command) {
        var user = userRepository.findByUsername(new Username(command.username()))
                .orElseThrow(() -> new BadCredentialsException(INVALID_CREDENTIALS));
        if (!hashingService.matches(command.password(), user.getPassword().hash())) {
            throw new BadCredentialsException(INVALID_CREDENTIALS);
        }
        user.registerSignIn();
        publishDomainEvents(user);
        return Optional.of(new AuthenticatedUser(user, tokenService.generateToken(user)));
    }

    private void publishDomainEvents(User user) {
        user.domainEvents().forEach(eventPublisher::publishEvent);
        user.clearDomainEvents();
    }
}
