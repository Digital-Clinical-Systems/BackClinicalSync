package com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.persistence.jpa.converters;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.valueobjects.Username;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class UsernamePersistenceConverter implements AttributeConverter<Username, String> {

    @Override
    public String convertToDatabaseColumn(Username attribute) {
        return attribute == null ? null : attribute.username();
    }

    @Override
    public Username convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new Username(dbData);
    }
}
