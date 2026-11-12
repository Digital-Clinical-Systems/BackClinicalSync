package com.digitalclinicalsystems.clinicalsync.platform.shared.interfaces.rest.resources;

import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;

/**
 * Uniform error body returned by every endpoint (TS-07).
 */
public record ErrorResource(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path,
        List<FieldErrorResource> errors
) {
    public static ErrorResource of(HttpStatus status, String message, String path) {
        return new ErrorResource(Instant.now(), status.value(), status.getReasonPhrase(), message, path, List.of());
    }

    public static ErrorResource of(HttpStatus status, String message, String path, List<FieldErrorResource> errors) {
        return new ErrorResource(Instant.now(), status.value(), status.getReasonPhrase(), message, path, errors);
    }
}
