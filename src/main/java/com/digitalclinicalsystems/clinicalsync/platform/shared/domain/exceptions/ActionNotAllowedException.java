package com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions;

/**
 * Raised when the actor is not entitled to perform an action on an aggregate
 * (for example, a nurse issuing a medical order). Translated to 403.
 */
public class ActionNotAllowedException extends RuntimeException {

    public ActionNotAllowedException(String message) {
        super(message);
    }
}
