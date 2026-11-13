package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.services;

import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.valueobjects.BloodPressure;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.valueobjects.RiskLevel;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Thresholds copied from {@code risk-evaluator.ts} of the frontend.
 */
class RiskEvaluatorTest {

    @ParameterizedTest(name = "{0}/{1} mmHg, {2} bpm, {3} %, {4} C -> {5}")
    @CsvSource({
            // normal values
            "118, 74, 82, 97, 36.6, NORMAL",
            "160, 90, 110, 94, 37.9, NORMAL",
            "100, 60, 50, 94, 35.0, NORMAL",
            // warning thresholds
            "120, 80, 82, 93, 36.6, WARNING",
            "120, 80, 111, 97, 36.6, WARNING",
            "120, 80, 49, 97, 36.6, WARNING",
            "161, 90, 82, 97, 36.6, WARNING",
            "99, 70, 82, 97, 36.6, WARNING",
            "120, 80, 82, 97, 38.0, WARNING",
            // critical thresholds
            "120, 80, 82, 89, 36.6, CRITICAL",
            "120, 80, 131, 97, 36.6, CRITICAL",
            "120, 80, 39, 97, 36.6, CRITICAL",
            "181, 90, 82, 97, 36.6, CRITICAL",
            "89, 60, 82, 97, 36.6, CRITICAL",
            "100, 47, 82, 97, 36.6, NORMAL",
            "100, 46, 82, 97, 36.6, CRITICAL",
            "120, 80, 82, 97, 39.0, CRITICAL",
            "120, 80, 82, 97, 34.9, CRITICAL",
            "168, 94, 126, 89, 38.4, CRITICAL"
    })
    void derivesRiskLevelFromThresholds(int systolic, int diastolic, int heartRate, int oxygenSaturation,
                                        double temperature, RiskLevel expected) {
        var risk = RiskEvaluator.evaluate(new BloodPressure(systolic, diastolic), heartRate, oxygenSaturation, temperature);
        assertThat(risk).isEqualTo(expected);
    }
}
