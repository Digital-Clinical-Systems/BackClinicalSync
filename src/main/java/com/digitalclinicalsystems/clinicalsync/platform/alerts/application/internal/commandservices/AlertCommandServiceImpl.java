package com.digitalclinicalsystems.clinicalsync.platform.alerts.application.internal.commandservices;

import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.aggregates.Alert;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.commands.AcknowledgeAlertCommand;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.commands.RaiseAlertCommand;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.commands.ResolveAlertCommand;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.repositories.IAlertRepository;
import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.services.IAlertCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AlertCommandServiceImpl implements IAlertCommandService {

    private final IAlertRepository alertRepository;
    private final ApplicationEventPublisher eventPublisher;

    public AlertCommandServiceImpl(IAlertRepository alertRepository, ApplicationEventPublisher eventPublisher) {
        this.alertRepository = alertRepository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Raises an alert for a trigger source. A source raises at most one alert,
     * so a replayed event never duplicates it.
     */
    @Override
    public Optional<Alert> handle(RaiseAlertCommand command) {
        var alert = new Alert(command);
        if (alertRepository.existsByTriggerSource(alert.getTriggerSource())) {
            return Optional.empty();
        }
        var saved = alertRepository.save(alert);
        saved.registerCreationEvents();
        publishDomainEvents(saved);
        return Optional.of(saved);
    }

    @Override
    public Optional<Alert> handle(AcknowledgeAlertCommand command) {
        var alert = alertRepository.findById(command.alertId())
                .orElseThrow(() -> new ResourceNotFoundException("Alert", command.alertId()));
        alert.acknowledge(command.acknowledgedBy());
        var updated = alertRepository.update(alert);
        publishDomainEvents(alert);
        return Optional.of(updated);
    }

    @Override
    public Optional<Alert> handle(ResolveAlertCommand command) {
        var alert = alertRepository.findById(command.alertId())
                .orElseThrow(() -> new ResourceNotFoundException("Alert", command.alertId()));
        alert.resolve(command.resolvedBy());
        var updated = alertRepository.update(alert);
        publishDomainEvents(alert);
        return Optional.of(updated);
    }

    private void publishDomainEvents(Alert alert) {
        alert.domainEvents().forEach(eventPublisher::publishEvent);
        alert.clearDomainEvents();
    }
}
