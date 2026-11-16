package com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.resources;

/**
 * State change the frontend sends with {@code PATCH /alerts/{id}}.
 */
public record FrontendAlertChangeResource(
        String status,
        String acknowledgedBy
) {
}
