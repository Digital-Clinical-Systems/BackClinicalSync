package com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.valueobjects;

/**
 * An admitted patient always has a unit and a bed assigned.
 */
public record BedLocation(String unit, String bed) {

    public BedLocation {
        if (unit == null || unit.isBlank() || bed == null || bed.isBlank()) {
            throw new IllegalArgumentException("An admitted patient requires an assigned unit and bed");
        }
        unit = unit.trim();
        bed = bed.trim();
    }

    @Override
    public String toString() {
        return unit + " - " + bed;
    }
}
