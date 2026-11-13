package com.digitalclinicalsystems.clinicalsync.platform.alerts.application.internal.outboundservices.acl;

import com.digitalclinicalsystems.clinicalsync.platform.iam.interfaces.acl.IamContextFacade;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Outbound ACL of BC-04 towards IAM: resolves who acknowledges or resolves an alert.
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
}
