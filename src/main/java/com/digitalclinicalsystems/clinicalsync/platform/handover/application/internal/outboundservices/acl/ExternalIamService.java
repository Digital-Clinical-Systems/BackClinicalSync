package com.digitalclinicalsystems.clinicalsync.platform.handover.application.internal.outboundservices.acl;

import com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.acl.IamContextFacade;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Outbound ACL of BC-05 towards IAM: resolves the outgoing nurse and validates the incoming one.
 */
@Service
public class ExternalIamService {

    private final IamContextFacade iamContextFacade;

    public ExternalIamService(IamContextFacade iamContextFacade) {
        this.iamContextFacade = iamContextFacade;
    }

    public UserId fetchAuthenticatedUserId() {
        var userId = iamContextFacade.fetchAuthenticatedUserId();
        if (userId == null || userId == 0L) {
            throw new AuthenticationCredentialsNotFoundException("Authentication is required to perform this action");
        }
        return new UserId(userId);
    }

    public Set<Roles> fetchAuthenticatedUserRoles() {
        return iamContextFacade.fetchAuthenticatedUserRoles().stream()
                .map(Roles::fromName)
                .collect(Collectors.toSet());
    }

    public void ensureUserIsNurse(UserId userId) {
        if (!iamContextFacade.existsUserById(userId.userId())) {
            throw new ResourceNotFoundException("User", userId.userId());
        }
        if (!iamContextFacade.fetchUserRolesById(userId.userId()).contains(Roles.NURSE.name())) {
            throw new IllegalArgumentException("The incoming nurse must be a user with the NURSE role");
        }
    }
}
