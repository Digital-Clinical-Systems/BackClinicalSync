package com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.aggregates;

import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.commands.IssueHandoverCommand;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.valueobjects.HandoverStatus;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ActionNotAllowedException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.BusinessRuleViolationException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HandoverTest {

    private static final UserId OUTGOING = new UserId(1L);
    private static final UserId INCOMING = new UserId(2L);

    private static IssueHandoverCommand command(UserId outgoing, Set<Roles> roles, UserId incoming, String situation) {
        return new IssueHandoverCommand(new PatientId(1L), outgoing, roles, incoming,
                situation, "Admitted for STEMI", "Stable", "Check vital signs every two hours");
    }

    @Test
    void rejectsEmptySbarSection() {
        assertThatThrownBy(() -> new Handover(command(OUTGOING, Set.of(Roles.NURSE), INCOMING, "   ")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("An SBAR handover cannot be issued with empty sections: Situation");
    }

    @Test
    void rejectsIncomingNurseEqualToOutgoing() {
        assertThatThrownBy(() -> new Handover(command(OUTGOING, Set.of(Roles.NURSE), OUTGOING, "Post MI day 2")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The incoming nurse must be different from the outgoing nurse");
    }

    @Test
    void onlyNursingStaffIssuesTheHandover() {
        assertThatThrownBy(() -> new Handover(command(OUTGOING, Set.of(Roles.PHYSICIAN), INCOMING, "Post MI day 2")))
                .isInstanceOf(ActionNotAllowedException.class);
    }

    @Test
    void onlyTheIncomingNurseAcknowledges() {
        var handover = new Handover(command(OUTGOING, Set.of(Roles.NURSE), INCOMING, "Post MI day 2"));
        assertThatThrownBy(() -> handover.acknowledge(OUTGOING)).isInstanceOf(ActionNotAllowedException.class);
        assertThat(handover.isPending()).isTrue();
    }

    @Test
    void rejectsSecondAcknowledgement() {
        var handover = new Handover(command(OUTGOING, Set.of(Roles.NURSE), INCOMING, "Post MI day 2"));
        handover.acknowledge(INCOMING);
        assertThat(handover.getStatus()).isEqualTo(HandoverStatus.ACKNOWLEDGED);
        assertThat(handover.getAcknowledgedAt()).isNotNull();
        assertThatThrownBy(() -> handover.acknowledge(INCOMING))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("The handover has already been acknowledged");
    }
}
