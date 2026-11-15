package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.valueobjects;

/**
 * Value object of BC-03.
 * Invariants: systolic is greater than diastolic and both fall inside the
 * admitted physiological range (values copied from {@code blood-pressure.vo.ts}).
 */
public record BloodPressure(Integer systolic, Integer diastolic) {

    public static final int SYSTOLIC_MIN = 50;
    public static final int SYSTOLIC_MAX = 260;
    public static final int DIASTOLIC_MIN = 30;
    public static final int DIASTOLIC_MAX = 160;

    public BloodPressure {
        if (systolic == null || diastolic == null) {
            throw new IllegalArgumentException("Blood pressure requires systolic and diastolic values");
        }
        if (systolic < SYSTOLIC_MIN || systolic > SYSTOLIC_MAX) {
            throw new IllegalArgumentException("Systolic pressure out of physiological range (%d-%d mmHg)"
                    .formatted(SYSTOLIC_MIN, SYSTOLIC_MAX));
        }
        if (diastolic < DIASTOLIC_MIN || diastolic > DIASTOLIC_MAX) {
            throw new IllegalArgumentException("Diastolic pressure out of physiological range (%d-%d mmHg)"
                    .formatted(DIASTOLIC_MIN, DIASTOLIC_MAX));
        }
        if (systolic <= diastolic) {
            throw new IllegalArgumentException("Systolic pressure must be greater than diastolic pressure");
        }
    }

    public int meanArterialPressure() {
        return (int) Math.round((systolic + 2.0 * diastolic) / 3.0);
    }

    @Override
    public String toString() {
        return systolic + "/" + diastolic;
    }
}
