package com.digitalclinicalsystems.clinicalsync.platform.audit.infrastructure.persistence.jpa.converters;

import com.digitalclinicalsystems.clinicalsync.platform.audit.domain.model.valueobjects.ActionType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class ActionTypePersistenceConverter implements AttributeConverter<ActionType, String> {

    @Override
    public String convertToDatabaseColumn(ActionType attribute) {
        return attribute == null ? null : attribute.actionType();
    }

    @Override
    public ActionType convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new ActionType(dbData);
    }
}
