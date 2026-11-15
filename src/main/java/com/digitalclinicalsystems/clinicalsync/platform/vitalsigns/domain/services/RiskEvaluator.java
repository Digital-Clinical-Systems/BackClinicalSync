package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.services;

import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.valueobjects.BloodPressure;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.valueobjects.RiskLevel;

/**
 * Domain service of BC-03: derives the risk level from the measured values.
 * Thresholds are the reference values of {@code risk-evaluator.ts} for
 * cardiovascular units; they must be reviewed with clinical staff before real use.
 */
public final class RiskEvaluator {

    private RiskEvaluator() {
    }

    public static RiskLevel evaluate(BloodPressure bloodPressure, int heartRate, int oxygenSaturation, double temperature) {
        var systolic = bloodPressure.systolic();
        var critical = oxygenSaturation < 90
                || heartRate < 40 || heartRate > 130
                || systolic < 90 || systolic > 180
                || bloodPressure.meanArterialPressure() < 65
                || temperature < 35 || temperature >= 39;
        if (critical) {
            return RiskLevel.CRITICAL;
        }
        var warning = oxygenSaturation < 94
                || heartRate < 50 || heartRate > 110
                || systolic < 100 || systolic > 160
                || temperature >= 38;
        return warning ? RiskLevel.WARNING : RiskLevel.NORMAL;
    }
}
