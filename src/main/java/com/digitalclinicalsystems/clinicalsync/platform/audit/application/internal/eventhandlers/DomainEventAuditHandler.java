package com.digitalclinicalsystems.clinicalsync.platform.audit.application.internal.eventhandlers;

import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.commands.CreateAuditLogCommand;
import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.services.IAuditLogCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.events.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import tools.jackson.databind.json.JsonMapper;

/**
 * Conformist towards every context: listens to any {@link DomainEvent} and
 * normalizes it into an audit entry. Audit is a sink, so a failure here is logged
 * and never breaks the operation that produced the event.
 */
@Slf4j
@Service
public class DomainEventAuditHandler {

    private final IAuditLogCommandService auditLogCommandService;
    private final JsonMapper jsonMapper;

    public DomainEventAuditHandler(IAuditLogCommandService auditLogCommandService, JsonMapper jsonMapper) {
        this.auditLogCommandService = auditLogCommandService;
        this.jsonMapper = jsonMapper;
    }

    @EventListener
    public void on(DomainEvent event) {
        try {
            auditLogCommandService.handle(new CreateAuditLogCommand(
                    event.getClass().getSimpleName(),
                    event.occurredAt(),
                    event.patientId(),
                    event.affectedResource(),
                    event.actorId(),
                    jsonMapper.writeValueAsString(event)));
        } catch (RuntimeException e) {
            log.error("Could not append audit log for {}", event.getClass().getSimpleName(), e);
        }
    }
}
