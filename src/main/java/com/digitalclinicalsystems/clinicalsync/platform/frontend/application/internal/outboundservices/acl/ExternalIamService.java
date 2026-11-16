package com.digitalclinicalsystems.clinicalsync.platform.frontend.application.internal.outboundservices.acl;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.entities.Role;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.queries.GetAllUsersQuery;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services.IUserQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.exceptions.ResourceNotFoundException;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Directory of professionals as the frontend knows them: by username
 * ({@code enf-001}), which is the identifier its role selector uses.
 */
@Service("frontendExternalIamService")
public class ExternalIamService {

    private final IUserQueryService userQueryService;

    public ExternalIamService(IUserQueryService userQueryService) {
        this.userQueryService = userQueryService;
    }

    public Directory directory() {
        var users = userQueryService.handle(new GetAllUsersQuery()).stream()
                .map(user -> new Professional(user.getId(), user.getUsername(), user.getFullName(),
                        user.getPrimaryRole(), user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()),
                        user.getShift(), user.getSpecialty()))
                .toList();
        return new Directory(
                users.stream().collect(Collectors.toMap(Professional::id, Function.identity())),
                users.stream().collect(Collectors.toMap(Professional::username, Function.identity())));
    }

    public record Professional(Long id, String username, String fullName, Roles primaryRole, Set<Roles> roles,
                               String shift, String specialty) {
    }

    public record Directory(Map<Long, Professional> byId, Map<String, Professional> byUsername) {

        /** Username shown to the frontend, or {@code sistema} for actions the system performed. */
        public String usernameOf(Long userId) {
            if (userId == null) {
                return null;
            }
            return Optional.ofNullable(byId.get(userId)).map(Professional::username).orElse(String.valueOf(userId));
        }

        /** Professional referenced by the frontend, by username or by numeric id. */
        public Professional resolve(String reference) {
            if (reference != null && byUsername.containsKey(reference)) {
                return byUsername.get(reference);
            }
            if (reference != null && !reference.isBlank() && reference.chars().allMatch(Character::isDigit)
                    && byId.containsKey(Long.valueOf(reference))) {
                return byId.get(Long.valueOf(reference));
            }
            throw new ResourceNotFoundException("User " + reference + " not found");
        }
    }
}
