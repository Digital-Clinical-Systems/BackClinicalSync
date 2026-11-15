package com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.authorization.sfs.pipeline;

import com.digitalclinicalsystems.clinicalsync.platform.shared.interfaces.rest.resources.ErrorResource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

/**
 * Writes the uniform error body when the authenticated user lacks the role
 * required by a resource (403).
 */
@Component
public class ForbiddenRequestHandler implements AccessDeniedHandler {

    private final JsonMapper jsonMapper;

    public ForbiddenRequestHandler(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {
        var body = ErrorResource.of(HttpStatus.FORBIDDEN, "Your role is not allowed to perform this action", request.getRequestURI());
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(jsonMapper.writeValueAsString(body));
    }
}
