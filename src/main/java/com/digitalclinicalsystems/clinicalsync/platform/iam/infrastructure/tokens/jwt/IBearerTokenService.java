package com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.tokens.jwt;

import com.digitalclinicalsystems.clinicalsync.platform.iam.application.internal.outboundservices.tokens.ITokenService;
import jakarta.servlet.http.HttpServletRequest;

public interface IBearerTokenService extends ITokenService {
    String getBearerTokenFrom(HttpServletRequest request);
}
