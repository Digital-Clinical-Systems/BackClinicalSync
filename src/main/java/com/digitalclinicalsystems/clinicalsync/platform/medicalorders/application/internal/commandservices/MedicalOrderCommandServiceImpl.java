package com.digitalclinicalsystems.clinicalsync.platform.medicalorders.application.internal.commandservices;

import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.application.internal.outboundservices.acl.ExternalPatientService;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.aggregates.MedicalOrder;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.commands.FulfillMedicalOrderCommand;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.commands.IssueMedicalOrderCommand;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.repositories.IMedicalOrderRepository;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.services.IMedicalOrderCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MedicalOrderCommandServiceImpl implements IMedicalOrderCommandService {

    private final IMedicalOrderRepository medicalOrderRepository;
    private final ExternalPatientService externalPatientService;
    private final ApplicationEventPublisher eventPublisher;

    public MedicalOrderCommandServiceImpl(IMedicalOrderRepository medicalOrderRepository,
                                          ExternalPatientService externalPatientService,
                                          ApplicationEventPublisher eventPublisher) {
        this.medicalOrderRepository = medicalOrderRepository;
        this.externalPatientService = externalPatientService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Issues an order and, when it replaces another one, leaves the previous order
     * SUPERSEDED in the same transaction: both belong to this bounded context.
     */
    @Override
    @Transactional
    public Optional<MedicalOrder> handle(IssueMedicalOrderCommand command) {
        var order = new MedicalOrder(command);
        externalPatientService.ensurePatientIsAdmitted(order.getPatientId());
        var previous = command.replacesOrderId() == null ? null
                : medicalOrderRepository.findById(command.replacesOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Medical order", command.replacesOrderId()));

        var saved = medicalOrderRepository.save(order);
        saved.registerCreationEvents();
        if (previous != null) {
            previous.supersede(saved);
            medicalOrderRepository.update(previous);
        }
        publishDomainEvents(saved);
        if (previous != null) {
            publishDomainEvents(previous);
        }
        return Optional.of(saved);
    }

    @Override
    public Optional<MedicalOrder> handle(FulfillMedicalOrderCommand command) {
        var order = medicalOrderRepository.findById(command.orderId())
                .orElseThrow(() -> new ResourceNotFoundException("Medical order", command.orderId()));
        order.fulfill(command.fulfilledBy(), command.fulfillerRoles());
        var updated = medicalOrderRepository.update(order);
        publishDomainEvents(order);
        return Optional.of(updated);
    }

    private void publishDomainEvents(MedicalOrder order) {
        order.domainEvents().forEach(eventPublisher::publishEvent);
        order.clearDomainEvents();
    }
}
