package com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.tokens.jwt;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services.ITokenService;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Extends the token port with how the token travels over HTTP. Reading the
 * Authorization header is a transport concern, so it stays out of the domain.
 */
public interface BearerTokenService extends ITokenService {
    String getBearerTokenFrom(HttpServletRequest request);
}
