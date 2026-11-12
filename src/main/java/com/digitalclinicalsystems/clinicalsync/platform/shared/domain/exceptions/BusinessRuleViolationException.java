package com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions;

/**
 * Raised when an operation conflicts with the current state of an aggregate
 * (for example, resolving an alert that was never acknowledged). Translated to 409.
 */
public class BusinessRuleViolationException extends RuntimeException {

    public BusinessRuleViolationException(String message) {
        super(message);
    }
}
