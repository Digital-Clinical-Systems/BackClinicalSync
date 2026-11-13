package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.aggregates;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.events.UserSignedInEvent;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.events.UserSignedUpEvent;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.valueobjects.HashedPassword;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.valueobjects.Username;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.AbstractDomainAggregateRoot;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.BusinessRuleViolationException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;

import java.time.Instant;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Aggregate root of BC-01. Single source of identity: no other context creates
 * users or decides which roles a professional holds.
 *
 * Invariants: username and full name are mandatory; a user always holds at least
 * one role, because every endpoint authorises by role and a user without roles
 * could sign in and do nothing; the password is only ever held hashed; a
 * deactivated user cannot open a session.
 */
public class User extends AbstractDomainAggregateRoot<User> {

    private Long id;
    private Username username;
    private String fullName;
    private HashedPassword password;
    private Set<Roles> roles;
    private boolean active;

    private User() {
    }

    public User(Username username, String fullName, HashedPassword password, Set<Roles> roles) {
        if (username == null) {
            throw new IllegalArgumentException("Username is required");
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (password == null) {
            throw new IllegalArgumentException("Password is required");
        }
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("A user must hold at least one role");
        }
        this.username = username;
        this.fullName = fullName.trim();
        this.password = password;
        this.roles = EnumSet.copyOf(roles);
        this.active = true;
    }

    public static User reconstitute(
            Long id,
            Username username,
            String fullName,
            HashedPassword password,
            Set<Roles> roles,
            boolean active) {
        var user = new User();
        user.id = id;
        user.username = username;
        user.fullName = fullName;
        user.password = password;
        user.roles = roles == null || roles.isEmpty() ? EnumSet.noneOf(Roles.class) : EnumSet.copyOf(roles);
        user.active = active;
        return user;
    }

    /** Records the sign-up event once the user has an identity. */
    public void registerCreationEvents() {
        registerDomainEvents(new UserSignedUpEvent(id, username.username(), roleNames(), Instant.now()));
    }

    /**
     * Confirms the user may open a session and records it. Credential checking is
     * infrastructure's job; deciding whether the account may be used is the
     * aggregate's.
     */
    public void registerSignIn() {
        if (!active) {
            throw new BusinessRuleViolationException("The user account is deactivated");
        }
        registerDomainEvents(new UserSignedInEvent(id, username.username(), Instant.now()));
    }

    public boolean hasRole(Roles role) {
        return roles.contains(role);
    }

    public Set<String> roleNames() {
        return roles.stream().map(Roles::name).collect(Collectors.toUnmodifiableSet());
    }

    public Long getId() {
        return id;
    }

    public Username getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public HashedPassword getPassword() {
        return password;
    }

    public Set<Roles> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public boolean isActive() {
        return active;
    }
}
