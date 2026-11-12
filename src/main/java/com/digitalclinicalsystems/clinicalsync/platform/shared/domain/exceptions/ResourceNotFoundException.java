package com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions;

/**
 * Raised when a referenced aggregate does not exist. Translated to 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Long id) {
        super("%s with id %d not found".formatted(resourceName, id));
    }
}
