package com.digitalclinicalsystems.clinicalsync.platform.iam.application.internal.outboundservices.tokens;

public interface ITokenService {
    String generateToken(String username);
    String getUsernameFromToken(String token);
    boolean validateToken(String token);
}
