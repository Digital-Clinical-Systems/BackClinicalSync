package com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.persistence.jpa.adapters;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.entities.Role;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.repositories.IRoleRepository;
import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.persistence.jpa.entities.RolePersistenceEntity;
import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.persistence.jpa.repositories.RolePersistenceRepository;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RoleRepositoryAdapterImpl implements IRoleRepository {

    private final RolePersistenceRepository rolePersistenceRepository;

    public RoleRepositoryAdapterImpl(RolePersistenceRepository rolePersistenceRepository) {
        this.rolePersistenceRepository = rolePersistenceRepository;
    }

    @Override
    public Optional<Role> findByName(Roles name) {
        return rolePersistenceRepository.findByName(name).map(RoleRepositoryAdapterImpl::toDomain);
    }

    @Override
    public boolean existsByName(Roles name) {
        return rolePersistenceRepository.existsByName(name);
    }

    @Override
    public List<Role> findAll() {
        return rolePersistenceRepository.findAll().stream().map(RoleRepositoryAdapterImpl::toDomain).toList();
    }

    @Override
    public Role save(Role role) {
        var entity = new RolePersistenceEntity();
        entity.setName(role.getName());
        return toDomain(rolePersistenceRepository.save(entity));
    }

    @Override
    public Role update(Role role) {
        var entity = rolePersistenceRepository.findById(role.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Role", role.getId()));
        entity.setName(role.getName());
        return toDomain(rolePersistenceRepository.save(entity));
    }

    @Override
    public Optional<Role> findById(Long id) {
        return rolePersistenceRepository.findById(id).map(RoleRepositoryAdapterImpl::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        rolePersistenceRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        rolePersistenceRepository.deleteAll();
    }

    static Role toDomain(RolePersistenceEntity entity) {
        return Role.reconstitute(entity.getId(), entity.getName());
    }
}
