package com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.persistence.jpa.adapters;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.aggregates.User;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.entities.Role;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.repositories.IUserRepository;
import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.persistence.jpa.entities.RolePersistenceEntity;
import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;
import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.persistence.jpa.repositories.RolePersistenceRepository;
import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.persistence.jpa.repositories.UserPersistenceRepository;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryAdapterImpl implements IUserRepository {

    private final UserPersistenceRepository userPersistenceRepository;
    private final RolePersistenceRepository rolePersistenceRepository;

    public UserRepositoryAdapterImpl(UserPersistenceRepository userPersistenceRepository,
                                     RolePersistenceRepository rolePersistenceRepository) {
        this.userPersistenceRepository = userPersistenceRepository;
        this.rolePersistenceRepository = rolePersistenceRepository;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userPersistenceRepository.findByUsername(username).map(UserRepositoryAdapterImpl::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userPersistenceRepository.existsByUsername(username);
    }

    @Override
    public List<User> findAll() {
        return userPersistenceRepository.findAll().stream().map(UserRepositoryAdapterImpl::toDomain).toList();
    }

    @Override
    public User save(User user) {
        var entity = new UserPersistenceEntity();
        copyToEntity(user, entity);
        return toDomain(userPersistenceRepository.save(entity));
    }

    @Override
    public User update(User user) {
        var entity = userPersistenceRepository.findById(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", user.getId()));
        copyToEntity(user, entity);
        return toDomain(userPersistenceRepository.save(entity));
    }

    @Override
    public Optional<User> findById(Long id) {
        return userPersistenceRepository.findById(id).map(UserRepositoryAdapterImpl::toDomain);
    }

    @Override
    public void deleteById(Long id) {
        userPersistenceRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        userPersistenceRepository.deleteAll();
    }

    private void copyToEntity(User user, UserPersistenceEntity entity) {
        entity.setUsername(user.getUsername());
        entity.setPassword(user.getPassword());
        entity.setFullName(user.getFullName());
        entity.setShift(user.getShift());
        entity.setSpecialty(user.getSpecialty());
        entity.setRoles(toRoleEntities(user.getRoles()));
    }

    private Set<RolePersistenceEntity> toRoleEntities(Set<Role> roles) {
        return roles.stream()
                .map(role -> rolePersistenceRepository.findByName(role.getName())
                        .orElseThrow(() -> new ResourceNotFoundException("Role %s not found".formatted(role.getStringName()))))
                .collect(Collectors.toCollection(HashSet::new));
    }

    static User toDomain(UserPersistenceEntity entity) {
        return User.reconstitute(
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getFullName(),
                entity.getRoles().stream().map(RoleRepositoryAdapterImpl::toDomain).toList(),
                entity.getShift(),
                entity.getSpecialty());
    }
}
