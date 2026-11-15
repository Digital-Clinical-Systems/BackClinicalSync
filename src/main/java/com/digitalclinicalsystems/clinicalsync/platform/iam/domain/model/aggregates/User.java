package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.aggregates;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.entities.Role;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.AbstractDomainAggregateRoot;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Aggregate root of BC-01 (IAM).
 * Invariants: username, credential and full name are mandatory; a user has at least one role.
 */
public class User extends AbstractDomainAggregateRoot<User> {

    private Long id;
    private String username;
    private String password;
    private String fullName;
    private Set<Role> roles;
    private String shift;
    private String specialty;

    public User(String username, String password, String fullName, List<Role> roles, String shift, String specialty) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or blank");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be null or blank");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name cannot be null or blank");
        }
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("A user must have at least one role");
        }
        this.username = username.trim();
        this.password = password;
        this.fullName = fullName.trim();
        this.roles = new HashSet<>(roles);
        this.shift = shift;
        this.specialty = specialty;
    }

    public static User reconstitute(
            Long id,
            String username,
            String password,
            String fullName,
            List<Role> roles,
            String shift,
            String specialty) {
        var user = new User(username, password, fullName, roles, shift, specialty);
        user.id = id;
        return user;
    }

    public boolean hasRole(Roles role) {
        return roles.stream().anyMatch(r -> r.getName() == role);
    }

    /** Role shown by the frontend directory, which models a single role per user. */
    public Roles getPrimaryRole() {
        return roles.stream().map(Role::getName).sorted().findFirst().orElseThrow();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    public Set<Role> getRoles() {
        return Set.copyOf(roles);
    }

    public String getShift() {
        return shift;
    }

    public String getSpecialty() {
        return specialty;
    }
}
