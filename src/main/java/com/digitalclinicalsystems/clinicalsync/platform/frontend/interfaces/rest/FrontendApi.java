package com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest;

/**
 * Backend for Frontend: exposes the bounded contexts with the exact contract the
 * Angular frontend consumes (the one of its former fake API), so it connects to
 * this backend by changing only its base URL.
 * <p>
 * The frontend has no sign-in yet: it identifies the acting professional in each
 * request body (recordedBy, prescribedBy, outgoingNurseId...). These routes are
 * therefore public; set {@code FRONTEND_API_ENABLED=false} to turn them off.
 */
public final class FrontendApi {

    public static final String BASE_PATH = "/api/frontend";
    public static final String ENABLED_PROPERTY = "clinicalsync.frontend-api.enabled";
    public static final String DESCRIPTION =
            "Contract consumed by FrontClinicalSync. Public: the acting professional travels in the request body.";

    private FrontendApi() {
    }
}
