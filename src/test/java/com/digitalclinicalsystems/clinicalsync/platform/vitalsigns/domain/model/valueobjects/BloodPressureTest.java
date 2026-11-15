package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.valueobjects;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BloodPressureTest {

    @ParameterizedTest
    @CsvSource({"49, 40", "261, 90"})
    void rejectsSystolicOutOfPhysiologicalRange(int systolic, int diastolic) {
        assertThatThrownBy(() -> new BloodPressure(systolic, diastolic))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Systolic pressure out of physiological range");
    }

    @ParameterizedTest
    @CsvSource({"120, 29", "200, 161"})
    void rejectsDiastolicOutOfPhysiologicalRange(int systolic, int diastolic) {
        assertThatThrownBy(() -> new BloodPressure(systolic, diastolic))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Diastolic pressure out of physiological range");
    }

    @ParameterizedTest
    @CsvSource({"80, 80", "90, 100"})
    void rejectsSystolicNotGreaterThanDiastolic(int systolic, int diastolic) {
        assertThatThrownBy(() -> new BloodPressure(systolic, diastolic))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Systolic pressure must be greater than diastolic pressure");
    }

    @Test
    void computesMeanArterialPressure() {
        assertThat(new BloodPressure(120, 80).meanArterialPressure()).isEqualTo(93);
        assertThat(new BloodPressure(118, 74).meanArterialPressure()).isEqualTo(89);
    }
}
