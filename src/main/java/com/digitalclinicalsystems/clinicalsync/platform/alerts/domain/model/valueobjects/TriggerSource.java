package com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects;

/**
 * Reference to the measurement or event that raised the alert, prefixed by the
 * originating context ({@code vital-sign:12}, {@code clinical-event:4}).
 */
public record TriggerSource(String triggerSource) {

    public TriggerSource {
        if (triggerSource == null || triggerSource.isBlank()) {
            throw new IllegalArgumentException("An alert cannot exist without the source that triggered it");
        }
        triggerSource = triggerSource.trim();
    }
}
