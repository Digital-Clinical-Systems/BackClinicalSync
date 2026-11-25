package com.digitalclinicalsystems.clinicalsync.platform.patients.application.internal.eventhandlers;

import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.commands.AdmitPatientCommand;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.queries.GetPatientByMedicalRecordNumberQuery;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.services.IPatientCommandService;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.services.IPatientQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.core.annotation.Order;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Seeds the eight demo patients of {@code db.json} in the
 * frontend, with the same medical record numbers, names, diagnoses, ages and beds.
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
    @Order(20)
    public void on(ApplicationReadyEvent event) {
        if (!seedDemoData) {
            return;
        }
        List.of(
                new AdmitPatientCommand("HC-48201", "Lucia Mendoza Rivas", "Infarto agudo de miocardio con elevacion del ST", 68, DEMO_UNIT, "Cama 1", null),
                new AdmitPatientCommand("HC-48207", "Hector Salazar Nunez", "Insuficiencia cardiaca descompensada", 74, DEMO_UNIT, "Cama 2", null),
                new AdmitPatientCommand("HC-48219", "Rosa Aguirre Tello", "Post operada de revascularizacion miocardica", 61, DEMO_UNIT, "Cama 3", null),
                new AdmitPatientCommand("HC-48226", "Marco Quispe Linares", "Angina inestable", 57, DEMO_UNIT, "Cama 4", null),
                new AdmitPatientCommand("HC-48233", "Elena Caceres Pardo", "Fibrilacion auricular de alta respuesta ventricular", 70, DEMO_UNIT, "Cama 5", null),
                new AdmitPatientCommand("HC-48240", "Julio Ramos Bendezu", "Post operado de reemplazo valvular aortico", 65, DEMO_UNIT, "Cama 6", null),
                new AdmitPatientCommand("HC-48248", "Carmen Ventura Rojas", "Edema agudo de pulmon", 79, DEMO_UNIT, "Cama 7", null),
                new AdmitPatientCommand("HC-48255", "Alberto Ninahuanca Soto", "Sindrome coronario agudo sin elevacion del ST", 54, DEMO_UNIT, "Cama 8", null)
        ).stream()
                .filter(command -> patientQueryService.handle(
                        new GetPatientByMedicalRecordNumberQuery(command.medicalRecordNumber())).isEmpty())
                .forEach(command -> {
                    patientCommandService.handle(command);
                    log.info("Seeded demo patient {}", command.medicalRecordNumber());
                });
    }
}
