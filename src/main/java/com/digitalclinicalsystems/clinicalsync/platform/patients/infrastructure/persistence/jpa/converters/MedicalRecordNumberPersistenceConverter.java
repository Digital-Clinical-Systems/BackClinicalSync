package com.digitalclinicalsystems.clinicalsync.platform.patients.infrastructure.persistence.jpa.converters;

import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.valueobjects.MedicalRecordNumber;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class MedicalRecordNumberPersistenceConverter implements AttributeConverter<MedicalRecordNumber, String> {

    @Override
    public String convertToDatabaseColumn(MedicalRecordNumber attribute) {
        return attribute == null ? null : attribute.medicalRecordNumber();
    }

    @Override
    public MedicalRecordNumber convertToEntityAttribute(String dbData) {
        return dbData == null ? null : new MedicalRecordNumber(dbData);
    }
}
