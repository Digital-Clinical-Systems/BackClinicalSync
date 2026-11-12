package com.digitalclinicalsystems.clinicalsync.platform.shared.domain;

import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.Collection;

public class AbstractDomainAggregateRoot <T extends  AbstractDomainAggregateRoot<T>>
extends AbstractAggregateRoot<T> {

    protected void registerDomainEvents(Object event) {
       super.registerEvent(event);
    }

    /**
     * Public so the application layer can publish the recorded events once the
     * aggregate is persisted: the domain object is not the JPA entity, so Spring
     * Data does not publish them on save.
     */
    @Override
    public void clearDomainEvents() {
        super.clearDomainEvents();
    }

    @Override
    public Collection<Object> domainEvents() {
        return super.domainEvents();
    }
}
