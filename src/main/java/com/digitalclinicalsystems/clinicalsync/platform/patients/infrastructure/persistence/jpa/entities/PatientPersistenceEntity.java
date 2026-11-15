package com.digitalclinicalsystems.clinicalsync.platform.patients.infrastructure.persistence.jpa.entities;

import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.valueobjects.BedLocation;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.valueobjects.MedicalRecordNumber;
import com.digitalclinicalsystems.clinicalsync.platform.patients.infrastructure.persistence.jpa.converters.MedicalRecordNumberPersistenceConverter;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.infrastructure.persistence.jpa.AuditableAbstractPersistenceEntity;
import com.digitalclinicalsystems.clinicalsync.platform.shared.infrastructure.persistence.jpa.converters.UserIdPersistenceConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
public class PatientPersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Convert(converter = MedicalRecordNumberPersistenceConverter.class)
    @Column(name = "medical_record_number", length = 50, nullable = false, unique = true)
    private MedicalRecordNumber medicalRecordNumber;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "admission_diagnosis")
    private String admissionDiagnosis;

    @Column(name = "age")
    private Integer age;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "unit", column = @Column(name = "unit", nullable = false)),
            @AttributeOverride(name = "bed", column = @Column(name = "bed", nullable = false))
    })
    private BedLocation bedLocation;

    @Column(name = "admitted_at", nullable = false)
    private Instant admittedAt;

    @Convert(converter = UserIdPersistenceConverter.class)
    @Column(name = "admitted_by")
    private UserId admittedBy;

    @Column(name = "active", nullable = false)
    private boolean active;

    @Column(name = "discharged_at")
    private Instant dischargedAt;

    @Convert(converter = UserIdPersistenceConverter.class)
    @Column(name = "discharged_by")
    private UserId dischargedBy;
}
