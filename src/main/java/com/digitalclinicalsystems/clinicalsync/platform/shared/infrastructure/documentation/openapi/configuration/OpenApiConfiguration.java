package com.digitalclinicalsystems.clinicalsync.platform.shared.infrastructure.documentation.openapi.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI documentation (TS-08), served at {@code /swagger-ui/index.html}.
 * Every endpoint requires a bearer token except authentication.
 */
@Configuration
public class OpenApiConfiguration {

    private static final String SECURITY_SCHEME_NAME = "bearerAuthentication";

    @Value("${spring.application.name}")
    private String applicationName;

    @Bean
    public OpenAPI clinicalSyncPlatformOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("ClinicalSync Platform API")
                        .description("RESTful API of ClinicalSync for cardiovascular care units: patients, vital signs, "
                                + "clinical events, alerts, SBAR handovers, medical orders and audit logs. "
                                + "Sign in at /api/v1/authentication/sign-in and use the token with the Authorize button.")
                        .version("v1.0.0")
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")))
                .externalDocs(new ExternalDocumentation()
                        .description("ClinicalSync project report")
                        .url("https://github.com/Digital-Clinical-Systems/Informe"))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
