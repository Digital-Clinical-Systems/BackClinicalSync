package com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.valueobjects;

import java.util.ArrayList;

/**
 * Value object of BC-07. Medication, dose, route and frequency are mandatory so
 * nursing can execute the order without asking the physician.
 */
public record Dosage(String medication, String dose, String route, String frequency) {

    public Dosage {
        var empty = new ArrayList<String>();
        if (isBlank(medication)) empty.add("medication");
        if (isBlank(dose)) empty.add("dose");
        if (isBlank(route)) empty.add("route");
        if (isBlank(frequency)) empty.add("frequency");
        if (!empty.isEmpty()) {
            throw new IllegalArgumentException("A medical order cannot be issued with empty data: " + String.join(", ", empty));
        }
        medication = medication.trim();
        dose = dose.trim();
        route = route.trim();
        frequency = frequency.trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
