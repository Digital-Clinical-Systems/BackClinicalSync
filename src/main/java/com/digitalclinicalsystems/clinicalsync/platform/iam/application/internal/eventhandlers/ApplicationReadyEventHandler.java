package com.digitalclinicalsystems.clinicalsync.platform.iam.application.internal.eventhandlers;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.commands.SignUpCommand;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.queries.GetUserByUsernameQuery;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services.IUserCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services.IUserQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Seeds the demo professionals listed in the README, with the same handles and
 * names the frontend directory uses, so the role selector of the web client lines
 * up with the users that actually exist here.
 *
 * Idempotent: a user that already exists is skipped, so restarting the service
 * never duplicates the directory, and the password comes from configuration rather
 * than being written into the code.
 */
@Slf4j
@Service
public class ApplicationReadyEventHandler {

    private record DemoUser(String username, String fullName, Set<Roles> roles) {
    }

    private static final List<DemoUser> DEMO_USERS = List.of(
            new DemoUser("enf-001", "Rocio Alarcon Vega", Set.of(Roles.NURSE)),
            new DemoUser("enf-002", "Daniel Figueroa Ponce", Set.of(Roles.NURSE)),
            new DemoUser("med-001", "Patricia Loayza Guerra", Set.of(Roles.PHYSICIAN)),
            new DemoUser("coord-001", "Miriam Tapia Cordova", Set.of(Roles.COORDINATOR)),
            new DemoUser("aud-001", "Hector Quispe Malpartida", Set.of(Roles.AUDITOR)));

    private final IUserCommandService userCommandService;
    private final IUserQueryService userQueryService;

    @Value("${clinicalsync.seed.demo-data}")
    private boolean seedDemoData;

    @Value("${clinicalsync.seed.demo-users-password}")
    private String demoUsersPassword;

    public ApplicationReadyEventHandler(IUserCommandService userCommandService,
                                        IUserQueryService userQueryService) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
    }

    @EventListener
    public void on(ApplicationReadyEvent event) {
        if (!seedDemoData) {
            return;
        }
        DEMO_USERS.stream()
                .filter(demoUser -> userQueryService.handle(
                        new GetUserByUsernameQuery(demoUser.username())).isEmpty())
                .forEach(demoUser -> {
                    userCommandService.handle(new SignUpCommand(
                            demoUser.username(), demoUser.fullName(), demoUsersPassword, demoUser.roles()));
                    log.info("Seeded demo user {} with roles {}", demoUser.username(), demoUser.roles());
                });
    }
}
