package com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.authorization.sfs.configuration;

import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.authorization.sfs.pipeline.BearerAuthorizationRequestFilter;
import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.authorization.sfs.pipeline.ForbiddenRequestHandler;
import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.authorization.sfs.pipeline.UnauthorizedRequestHandlerEntryPoint;
import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.tokens.jwt.IBearerTokenService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Stateless bearer-token security (TS-01). Role rules live next to each endpoint
 * through {@code @PreAuthorize}, so every bounded context declares its own access policy.
 */
@Configuration
@EnableMethodSecurity
public class WebSecurityConfiguration {

    private static final String[] PUBLIC_PATHS = {
            "/api/v1/authentication/**",
            // Backend for Frontend: the frontend identifies the actor in the request body
            "/api/frontend/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/webjars/**",
            "/error"
    };

    private final UserDetailsService userDetailsService;
    private final IBearerTokenService tokenService;
    private final UnauthorizedRequestHandlerEntryPoint unauthorizedRequestHandler;
    private final ForbiddenRequestHandler forbiddenRequestHandler;

    @Value("${cors.allowed-origins}")
    private String allowedOrigins;

    public WebSecurityConfiguration(@Qualifier("defaultUserDetailsService") UserDetailsService userDetailsService,
                                    IBearerTokenService tokenService,
                                    UnauthorizedRequestHandlerEntryPoint unauthorizedRequestHandler,
                                    ForbiddenRequestHandler forbiddenRequestHandler) {
        this.userDetailsService = userDetailsService;
        this.tokenService = tokenService;
        this.unauthorizedRequestHandler = unauthorizedRequestHandler;
        this.forbiddenRequestHandler = forbiddenRequestHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(unauthorizedRequestHandler)
                        .accessDeniedHandler(forbiddenRequestHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(new BearerAuthorizationRequestFilter(tokenService, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setExposedHeaders(List.of("Location"));
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
