package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.aggregates.User;

/**
 * Port for session tokens. The domain states that a session is represented by a
 * token tied to a username; that it is a JWT is an infrastructure decision.
 */
public interface ITokenService {
    String generateToken(User user);
    String getUsernameFromToken(String token);
    boolean validateToken(String token);
}
