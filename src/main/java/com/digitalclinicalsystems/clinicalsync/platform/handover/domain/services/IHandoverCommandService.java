package com.digitalclinicalsystems.clinicalsync.platform.handover.domain.services;

import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.aggregates.Handover;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.commands.AcknowledgeHandoverCommand;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.commands.IssueHandoverCommand;

import java.util.Optional;

public interface IHandoverCommandService {
    Optional<Handover> handle(IssueHandoverCommand command);
    Optional<Handover> handle(AcknowledgeHandoverCommand command);
}
