package com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.authorization.sfs.pipeline;

import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.authorization.sfs.model.UsernamePasswordAuthenticationTokenBuilder;
import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.tokens.jwt.IBearerTokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Authenticates the request from its bearer token. Requests without a valid
 * token continue unauthenticated and are rejected later by the authorization rules.
 */
@Slf4j
public class BearerAuthorizationRequestFilter extends OncePerRequestFilter {

    private final IBearerTokenService tokenService;
    private final UserDetailsService userDetailsService;

    public BearerAuthorizationRequestFilter(IBearerTokenService tokenService, UserDetailsService userDetailsService) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        var token = tokenService.getBearerTokenFrom(request);
        if (token != null && tokenService.validateToken(token)) {
            try {
                var username = tokenService.getUsernameFromToken(token);
                var userDetails = userDetailsService.loadUserByUsername(username);
                SecurityContextHolder.getContext().setAuthentication(
                        UsernamePasswordAuthenticationTokenBuilder.build(userDetails, request));
            } catch (UsernameNotFoundException e) {
                log.debug("Bearer token refers to an unknown user");
            }
        }
        filterChain.doFilter(request, response);
    }
}
