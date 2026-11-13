package com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.authorization.sfs.pipeline;

import com.digitalclinicalsystems.clinicalsync.platform.shared.interfaces.rest.resources.ErrorResource;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * A request without a usable token never reaches a controller, so the global
 * handler cannot translate it. This writes the same uniform error body (TS-07) the
 * client gets everywhere else.
 */
@Component
public class UnauthorizedRequestHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public UnauthorizedRequestHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), ErrorResource.of(
                HttpStatus.UNAUTHORIZED,
                "Authentication is required. Sign in at /api/v1/authentication/sign-in and send the token as 'Authorization: Bearer <token>'",
                request.getRequestURI()));
    }
}
