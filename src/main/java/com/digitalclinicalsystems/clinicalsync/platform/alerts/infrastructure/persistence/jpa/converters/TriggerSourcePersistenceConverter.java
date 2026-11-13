package com.digitalclinicalsystems.clinicalsync.platform.alerts.infrastructure.persistence.jpa.converters;

import com.digitalclinicalsystems.clinicalsync.platform.alerts.domain.model.valueobjects.TriggerSource;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class TriggerSourcePersistenceConverter implements AttributeConverter<TriggerSource, String> {

    @Override
    public String convertToDatabaseColumn(TriggerSource attribute) {
        return attribute == null ? null : attribute.triggerSource();
    }

    @Override
    public TriggerSource convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new TriggerSource(dbData);
    }
}
