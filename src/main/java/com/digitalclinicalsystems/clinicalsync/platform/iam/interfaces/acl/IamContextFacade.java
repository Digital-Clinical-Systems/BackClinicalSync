package com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.acl;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services.IUserQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Anti-corruption layer of BC-01. The only way the other contexts learn who is
 * acting and which roles they hold, expressed in primitives so no context has to
 * import the user aggregate: identity stays owned here.
 *
 * {@code fetchAuthenticatedUserId} answers 0 instead of throwing, so each calling
 * context decides for itself what a missing session means for its operation.
 */
@Service
public class IamContextFacade {

    private static final Long NO_AUTHENTICATED_USER = 0L;
    private static final String AUTHORITY_PREFIX = "ROLE_";

    private final IUserQueryService userQueryService;

    public IamContextFacade(IUserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    public Long fetchAuthenticatedUserId() {
        return authenticatedUserDetails().map(UserDetailsImpl::getId).orElse(NO_AUTHENTICATED_USER);
    }

    public Set<String> fetchAuthenticatedUserRoles() {
        return authenticatedUserDetails()
                .map(details -> details.getAuthorities().stream()
                        .map(authority -> stripPrefix(authority.getAuthority()))
                        .collect(Collectors.toUnmodifiableSet()))
                .orElseGet(Set::of);
    }

    public boolean existsUserById(Long userId) {
        return userId != null && userQueryService.handle(new GetUserByIdQuery(userId)).isPresent();
    }

    public Set<String> fetchUserRolesById(Long userId) {
        if (userId == null) {
            return Set.of();
        }
        return userQueryService.handle(new GetUserByIdQuery(userId))
                .map(user -> user.roleNames())
                .orElseGet(Set::of);
    }

    private static String stripPrefix(String authority) {
        return authority.startsWith(AUTHORITY_PREFIX) ? authority.substring(AUTHORITY_PREFIX.length()) : authority;
    }

    private Optional<UserDetailsImpl> authenticatedUserDetails() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }
        return authentication.getPrincipal() instanceof UserDetailsImpl details
                ? Optional.of(details)
                : Optional.empty();
    }
}
