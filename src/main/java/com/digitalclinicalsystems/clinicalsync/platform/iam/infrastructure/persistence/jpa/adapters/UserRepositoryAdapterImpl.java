package com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.persistence.jpa.adapters;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.aggregates.User;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.repositories.IUserRepository;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.valueobjects.HashedPassword;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.valueobjects.Username;
import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;
import com.digitalclinicalsystems.clinicalsync.platform.iam.infrastructure.persistence.jpa.repositories.UserPersistenceRepository;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;
import org.springframework.stereotype.Repository;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryAdapterImpl implements IUserRepository {

    private final UserPersistenceRepository userPersistenceRepository;

    public UserRepositoryAdapterImpl(UserPersistenceRepository userPersistenceRepository) {
        this.userPersistenceRepository = userPersistenceRepository;
    }

    @Override
    public Optional<User> findByUsername(Username username) {
        return userPersistenceRepository.findByUsername(username).map(UserRepositoryAdapterImpl::toDomain);
    }

    @Override
    public boolean existsByUsername(Username username) {
        return userPersistenceRepository.existsByUsername(username);
    }

    @Override
    public List<User> findAllByRole(Roles role) {
        return userPersistenceRepository.findAllByRole(role).stream()
                .map(UserRepositoryAdapterImpl::toDomain).toList();
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

    private static void copyToEntity(User user, UserPersistenceEntity entity) {
        entity.setUsername(user.getUsername());
        entity.setFullName(user.getFullName());
        entity.setPasswordHash(user.getPassword().hash());
        entity.setRoles(user.getRoles().isEmpty() ? EnumSet.noneOf(Roles.class) : EnumSet.copyOf(user.getRoles()));
        entity.setActive(user.isActive());
    }

    private static User toDomain(UserPersistenceEntity entity) {
        return User.reconstitute(
                entity.getId(),
                entity.getUsername(),
                entity.getFullName(),
                new HashedPassword(entity.getPasswordHash()),
                entity.getRoles(),
                entity.isActive());
    }
}
