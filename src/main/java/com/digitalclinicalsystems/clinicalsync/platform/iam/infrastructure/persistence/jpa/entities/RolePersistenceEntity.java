package com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.persistence.jpa.entities;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;
import com.digitalclinicalsystems.clinicalsync.platform.shared.infrastructure.persistence.jpa.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
public class RolePersistenceEntity extends AuditableAbstractPersistenceEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "name", length = 20, nullable = false, unique = true)
    private Roles name;
}
