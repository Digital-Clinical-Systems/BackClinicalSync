package com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.authorization.sfs.configuration;

import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.authorization.sfs.pipeline.BearerAuthorizationRequestFilter;
import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.authorization.sfs.pipeline.ForbiddenRequestHandler;
import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.authorization.sfs.pipeline.UnauthorizedRequestHandler;
import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Stateless bearer security and the role map of the API (TS-01).
 *
 * The role rules live here, in one place, and mirror the endpoint table of the
 * README. They are a coarse first gate: the aggregates still enforce their own
 * rules, because a role is not the same as the right to act on a given record (a
 * nurse may fulfil an order, but not one she issued herself). Authorisation that
 * depends on the data stays in the domain.
 *
 * Sign-in does not go through an AuthenticationManager: credentials are checked by
 * the IAM command service against the hashing port, so the chain only has to turn
 * a token into an authenticated user.
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration {

    private static final String[] PUBLIC_PATHS = {
            "/api/v1/authentication/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**",
            "/error"
    };

    private final BearerTokenService tokenService;
    private final UserDetailsService userDetailsService;
    private final UnauthorizedRequestHandler unauthorizedRequestHandler;
    private final ForbiddenRequestHandler forbiddenRequestHandler;

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    public WebSecurityConfiguration(BearerTokenService tokenService,
                                    UserDetailsService userDetailsService,
                                    UnauthorizedRequestHandler unauthorizedRequestHandler,
                                    ForbiddenRequestHandler forbiddenRequestHandler) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
        this.unauthorizedRequestHandler = unauthorizedRequestHandler;
        this.forbiddenRequestHandler = forbiddenRequestHandler;
    }

    @Bean
    public BearerAuthorizationRequestFilter bearerAuthorizationRequestFilter() {
        return new BearerAuthorizationRequestFilter(tokenService, userDetailsService);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(unauthorizedRequestHandler)
                        .accessDeniedHandler(forbiddenRequestHandler))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/vital-signs").hasRole("NURSE")
                        .requestMatchers(HttpMethod.POST, "/api/v1/clinical-events").hasAnyRole("NURSE", "PHYSICIAN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/handovers/*/acknowledge").hasRole("NURSE")
                        .requestMatchers(HttpMethod.POST, "/api/v1/handovers").hasRole("NURSE")
                        .requestMatchers(HttpMethod.POST, "/api/v1/medical-orders/*/fulfill").hasRole("NURSE")
                        .requestMatchers(HttpMethod.POST, "/api/v1/medical-orders").hasRole("PHYSICIAN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/audit-logs").hasAnyRole("COORDINATOR", "AUDITOR")
                        .anyRequest().authenticated())
                .addFilterBefore(bearerAuthorizationRequestFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
