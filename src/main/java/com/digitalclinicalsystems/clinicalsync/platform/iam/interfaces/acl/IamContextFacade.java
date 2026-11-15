package com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.acl;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.entities.Role;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.queries.GetUserByIdQuery;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services.IUserQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Anti-corruption layer of BC-01. Other bounded contexts ask IAM who the
 * authenticated actor is and which roles a user holds, using primitive types only.
 */
@Service
public class IamContextFacade {

    private static final String ROLE_PREFIX = "ROLE_";

    private final IUserQueryService userQueryService;

    public IamContextFacade(IUserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    /** @return the id of the authenticated user, or {@code 0L} when the request is anonymous. */
    public Long fetchAuthenticatedUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl userDetails) {
            return userDetails.getId();
        }
        return 0L;
    }

    /** @return role names (without the {@code ROLE_} prefix) of the authenticated user. */
    public List<String> fetchAuthenticatedUserRoles() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return List.of();
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority != null && authority.startsWith(ROLE_PREFIX))
                .map(authority -> authority.substring(ROLE_PREFIX.length()))
                .toList();
    }

    public boolean existsUserById(Long userId) {
        return userQueryService.handle(new GetUserByIdQuery(userId)).isPresent();
    }

    /** @return role names of the given user, empty when the user does not exist. */
    public List<String> fetchUserRolesById(Long userId) {
        return userQueryService.handle(new GetUserByIdQuery(userId))
                .map(user -> user.getRoles().stream().map(Role::getStringName).toList())
                .orElse(List.of());
    }
}
