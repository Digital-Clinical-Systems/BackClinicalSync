package com.digitalclinicalsystems.clinicalsync.platform.iam.application.internal.eventhandlers;

import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.commands.SeedRolesCommand;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.commands.SignUpCommand;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.queries.GetUserByUsernameQuery;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services.IRoleCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services.IUserCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services.IUserQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.Roles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Seeds the clinical roles and, when enabled, one demo user per role. The demo
 * identities mirror {@code db.json} of the frontend (fictitious people).
 */
@Slf4j
@Service
public class ApplicationReadyEventHandler {

    private final IRoleCommandService roleCommandService;
    private final IUserCommandService userCommandService;
    private final IUserQueryService userQueryService;

    @Value("${clinicalsync.seed.demo-data}")
    private boolean seedDemoData;

    @Value("${clinicalsync.seed.demo-users-password}")
    private String demoUsersPassword;

    public ApplicationReadyEventHandler(IRoleCommandService roleCommandService,
                                        IUserCommandService userCommandService,
                                        IUserQueryService userQueryService) {
        this.roleCommandService = roleCommandService;
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
    }

    @EventListener
    public void on(ApplicationReadyEvent event) {
        log.info("Verifying if roles seeding is needed");
        roleCommandService.handle(new SeedRolesCommand());
        if (seedDemoData) {
            seedDemoUsers();
        }
    }

    private void seedDemoUsers() {
        List.of(
                new SignUpCommand("enf-001", demoUsersPassword, "Rocio Alarcon Vega", List.of(Roles.NURSE), "Turno dia", null),
                new SignUpCommand("enf-002", demoUsersPassword, "Daniel Figueroa Ponce", List.of(Roles.NURSE), "Turno noche", null),
                new SignUpCommand("med-001", demoUsersPassword, "Patricia Loayza Guerra", List.of(Roles.PHYSICIAN), null, "Cardiologia"),
                new SignUpCommand("coord-001", demoUsersPassword, "Miriam Tapia Cordova", List.of(Roles.COORDINATOR), null, "Jefatura de enfermeria"),
                new SignUpCommand("aud-001", demoUsersPassword, "Auditoria Clinica Demo", List.of(Roles.AUDITOR), null, "Calidad y auditoria")
        ).stream()
                .filter(command -> userQueryService.handle(new GetUserByUsernameQuery(command.username())).isEmpty())
                .forEach(command -> {
                    userCommandService.handle(command);
                    log.info("Seeded demo user {}", command.username());
                });
    }
}
