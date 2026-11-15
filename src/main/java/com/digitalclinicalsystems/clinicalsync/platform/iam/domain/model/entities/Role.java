package com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.entities;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;

public class Role {

    private Long id;
    private Roles name;

    public Role(Roles name) {
        if (name == null) {
            throw new IllegalArgumentException("Role name cannot be null");
        }
        this.name = name;
    }

    public static Role reconstitute(Long id, Roles name) {
        var role = new Role(name);
        role.id = id;
        return role;
    }

    public String getStringName() {
        return name.name();
    }

    public Long getId() {
        return id;
    }

    public Roles getName() {
        return name;
    }
}
