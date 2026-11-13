package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.aggregates;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.events.UserSignedInEvent;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.events.UserSignedUpEvent;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.valueobjects.HashedPassword;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.valueobjects.Username;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.BusinessRuleViolationException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    private static final HashedPassword HASH = new HashedPassword("$2a$10$notARealHashJustForTheTest");

    private static User nurse(boolean active) {
        return User.reconstitute(1L, new Username("enf-001"), "Rocio Alarcon Vega", HASH,
                Set.of(Roles.NURSE), active);
    }

    @Test
    void aUserAlwaysHoldsAtLeastOneRole() {
        assertThatThrownBy(() -> new User(new Username("enf-003"), "Sin rol", HASH, Set.of()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A user must hold at least one role");
    }

    @Test
    void fullNameIsMandatory() {
        assertThatThrownBy(() -> new User(new Username("enf-003"), "   ", HASH, Set.of(Roles.NURSE)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Full name is required");
    }

    @Test
    void theUsernameIsNormalisedSoTheSamePersonIsNotRegisteredTwice() {
        var user = new User(new Username("  ENF-004 "), "Mayuscula Demo", HASH, Set.of(Roles.NURSE));
        assertThat(user.getUsername().username()).isEqualTo("enf-004");
    }

    @Test
    void aNewUserIsActiveAndAnnouncesItsRegistration() {
        var user = User.reconstitute(7L, new Username("med-001"), "Patricia Loayza Guerra", HASH,
                Set.of(Roles.PHYSICIAN), true);
        user.registerCreationEvents();

        assertThat(user.isActive()).isTrue();
        assertThat(user.hasRole(Roles.PHYSICIAN)).isTrue();
        assertThat(user.hasRole(Roles.NURSE)).isFalse();
        assertThat(user.domainEvents()).hasSize(1).first().isInstanceOf(UserSignedUpEvent.class);
    }

    @Test
    void signingInIsRecordedForTheAuditLog() {
        var user = nurse(true);
        user.registerSignIn();

        assertThat(user.domainEvents()).hasSize(1).first().isInstanceOf(UserSignedInEvent.class);
    }

    @Test
    void aDeactivatedUserCannotOpenASession() {
        var user = nurse(false);
        assertThatThrownBy(user::registerSignIn)
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("The user account is deactivated");
    }

    @Test
    void theRoleSetCannotBeChangedFromOutsideTheAggregate() {
        var roles = nurse(true).getRoles();
        assertThatThrownBy(() -> roles.add(Roles.AUDITOR))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
