package com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.valueobjects;

/**
 * Type of the audited action in UPPER_SNAKE_CASE, derived from the domain event
 * name ({@code VitalSignsRecordedEvent} becomes {@code VITAL_SIGNS_RECORDED}).
 */
public record ActionType(String actionType) {

    public ActionType {
        if (actionType == null || actionType.isBlank()) {
            throw new IllegalArgumentException("Every audit entry requires an action type");
        }
        actionType = actionType.trim().toUpperCase();
    }

    public static ActionType fromEventName(String eventName) {
        if (eventName == null || eventName.isBlank()) {
            throw new IllegalArgumentException("Every audit entry requires an action type");
        }
        var name = eventName.endsWith("Event") ? eventName.substring(0, eventName.length() - "Event".length()) : eventName;
        return new ActionType(name.replaceAll("([a-z0-9])([A-Z])", "$1_$2"));
    }
}
