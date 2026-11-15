package com.digitalclinicalsystems.clinicalsync.platform.patients.application.internal.eventhandlers;

import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.commands.AdmitPatientCommand;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.queries.GetPatientByMedicalRecordNumberQuery;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.services.IPatientCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.services.IPatientQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Seeds the three demo patients of {@code patient-inmemory.repository.ts} in the
 * frontend, with the same medical record numbers, diagnoses and beds.
 */
@Slf4j
@Service
public class ApplicationReadyEventHandler {

    private static final String DEMO_UNIT = "UCI Cardiovascular";

    private final IPatientCommandService patientCommandService;
    private final IPatientQueryService patientQueryService;

    @Value("${clinicalsync.seed.demo-data}")
    private boolean seedDemoData;

    public ApplicationReadyEventHandler(IPatientCommandService patientCommandService,
                                        IPatientQueryService patientQueryService) {
        this.patientCommandService = patientCommandService;
        this.patientQueryService = patientQueryService;
    }

    @EventListener
    public void on(ApplicationReadyEvent event) {
        if (!seedDemoData) {
            return;
        }
        List.of(
                new AdmitPatientCommand("HC-48201", "Paciente demo 1", "Infarto agudo de miocardio", null, DEMO_UNIT, "Cama 1", null),
                new AdmitPatientCommand("HC-48207", "Paciente demo 2", "Insuficiencia cardiaca descompensada", null, DEMO_UNIT, "Cama 2", null),
                new AdmitPatientCommand("HC-48219", "Paciente demo 3", "Post operado de revascularizacion", null, DEMO_UNIT, "Cama 3", null)
        ).stream()
                .filter(command -> patientQueryService.handle(
                        new GetPatientByMedicalRecordNumberQuery(command.medicalRecordNumber())).isEmpty())
                .forEach(command -> {
                    patientCommandService.handle(command);
                    log.info("Seeded demo patient {}", command.medicalRecordNumber());
                });
    }
}
