package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsernameTest {

    @Test
    void itRejectsBlankValues() {
        assertThatThrownBy(() -> new Username("  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Username is required");
    }

    @Test
    void itRejectsNull() {
        assertThatThrownBy(() -> new Username(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void itRejectsValuesLongerThanTheColumn() {
        assertThatThrownBy(() -> new Username("e".repeat(51)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("50");
    }

    @Test
    void twoSpellingsOfTheSameHandleAreEqual() {
        assertThat(new Username("ENF-001")).isEqualTo(new Username(" enf-001 "));
    }
}
