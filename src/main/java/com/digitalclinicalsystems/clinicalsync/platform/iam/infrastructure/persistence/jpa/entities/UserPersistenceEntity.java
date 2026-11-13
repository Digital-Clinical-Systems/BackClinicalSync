package com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.persistence.jpa.entities;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.valueobjects.Username;
import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.persistence.jpa.converters.UsernamePersistenceConverter;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;
import com.digitalclinicalsystems.clinicalsync.platform.shared.infrastructure.persistence.jpa.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.EnumSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Convert(converter = UsernamePersistenceConverter.class)
    @Column(name = "username", length = 50, nullable = false, unique = true)
    private Username username;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    /** Stored hashed, never in clear text. Never exposed by any resource. */
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    /**
     * Roles are a Shared Kernel enum, not an aggregate of their own: nothing in the
     * system needs a role to have its own lifecycle, so an element collection says
     * exactly what is true and keeps the user as the only aggregate of BC-01.
     * Eager because every authenticated request needs them to authorise.
     */
    @ElementCollection(targetClass = Roles.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private Set<Roles> roles = EnumSet.noneOf(Roles.class);

    @Column(name = "active", nullable = false)
    private boolean active;
}
