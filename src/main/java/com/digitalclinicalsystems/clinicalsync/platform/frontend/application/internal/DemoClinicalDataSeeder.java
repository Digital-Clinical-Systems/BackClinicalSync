package com.digitalclinicalsystems.clinicalsync.platform.frontend.application.internal;

import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.aggregates.ClinicalEvent;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.repositories.IClinicalEventRepository;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.valueobjects.ClinicalEventSeverity;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.domain.model.valueobjects.ClinicalEventType;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.aggregates.Handover;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.repositories.IHandoverRepository;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.valueobjects.HandoverStatus;
import com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.valueobjects.SbarContent;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.model.queries.GetUserByUsernameQuery;
import com.digitalclinicalsystems.clinicalsync.platform.iam.domain.services.IUserQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.aggregates.MedicalOrder;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.repositories.IMedicalOrderRepository;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.valueobjects.Dosage;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.domain.model.valueobjects.OrderStatus;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.model.queries.GetPatientByMedicalRecordNumberQuery;
import com.digitalclinicalsystems.clinicalsync.platform.patients.domain.services.IPatientQueryService;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.AbstractDomainAggregateRoot;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.aggregates.VitalSignRecord;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.repositories.IVitalSignRecordRepository;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.valueobjects.BloodPressure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

/**
 * Historia clinica de demostracion del turno en curso.
 *
 * Vive en el contexto que compone la vista del frontend porque una historia
 * coherente cruza todos los Bounded Contexts: una toma de signos vitales fuera
 * de umbral levanta una alerta, una indicacion se emite y se cumple, un turno se
 * traspasa. Ningun contexto por separado puede sembrar eso sin conocer a los
 * demas, y este es el unico que ya los compone.
 *
 * Reconstituye los agregados en lugar de emitir comandos, porque los comandos
 * fechan todo en el instante de ejecucion y la aplicacion razona sobre el
 * tiempo: un control se considera vencido pasadas seis horas, y con todo
 * fechado al arranque ningun paciente apareceria pendiente. Reconstituir valida
 * igualmente las invariantes y vuelve a derivar el nivel de riesgo, de modo que
 * la siembra no puede introducir un dato que el dominio rechazaria.
 *
 * Despues de guardar publica los eventos de cada agregado: asi las alertas y la
 * bitacora no se siembran a mano, sino que se generan por las mismas politicas
 * que operan en produccion. Las alertas quedan fechadas al arranque, que es
 * correcto: son las que estan abiertas ahora.
 */
@Slf4j
@Service
public class DemoClinicalDataSeeder {

    private static final String ENFERMERA_DIA = "enf-001";
    private static final String ENFERMERO_NOCHE = "enf-002";
    private static final String MEDICO = "med-001";

    private final IPatientQueryService patients;
    private final IUserQueryService users;
    private final IVitalSignRecordRepository vitalSigns;
    private final IClinicalEventRepository clinicalEvents;
    private final IMedicalOrderRepository medicalOrders;
    private final IHandoverRepository handovers;
    private final ApplicationEventPublisher publisher;

    @Value("${clinicalsync.seed.demo-data}")
    private boolean seedDemoData;

    /** Instante al que se ancla toda la historia sembrada. */
    private Instant ahora;

    public DemoClinicalDataSeeder(IPatientQueryService patients,
                                  IUserQueryService users,
                                  IVitalSignRecordRepository vitalSigns,
                                  IClinicalEventRepository clinicalEvents,
                                  IMedicalOrderRepository medicalOrders,
                                  IHandoverRepository handovers,
                                  ApplicationEventPublisher publisher) {
        this.patients = patients;
        this.users = users;
        this.vitalSigns = vitalSigns;
        this.clinicalEvents = clinicalEvents;
        this.medicalOrders = medicalOrders;
        this.handovers = handovers;
        this.publisher = publisher;
    }

    @EventListener
    @Order(30)
    public void on(ApplicationReadyEvent event) {
        if (!seedDemoData) {
            return;
        }
        // Idempotente: si ya hay registros clinicos, un reinicio no duplica la
        // historia ni reabre alertas que alguien cerro.
        if (!vitalSigns.findAllByFilters(null, null, null).isEmpty()) {
            log.info("Demo clinical history already present, skipping");
            return;
        }
        var enfermeraDia = usuario(ENFERMERA_DIA);
        var enfermeroNoche = usuario(ENFERMERO_NOCHE);
        var medico = usuario(MEDICO);
        if (enfermeraDia.isEmpty() || enfermeroNoche.isEmpty() || medico.isEmpty()) {
            log.warn("Demo users are not seeded yet, skipping the clinical history");
            return;
        }
        this.ahora = Instant.now();

        var lucia = paciente("HC-48201");
        var hector = paciente("HC-48207");
        var rosa = paciente("HC-48219");
        var marco = paciente("HC-48226");
        var elena = paciente("HC-48233");
        var julio = paciente("HC-48240");
        var carmen = paciente("HC-48248");
        if (lucia.isEmpty() || hector.isEmpty()) {
            log.warn("Demo patients are not seeded yet, skipping the clinical history");
            return;
        }

        var enfDia = enfermeraDia.get();
        var enfNoche = enfermeroNoche.get();
        var med = medico.get();

        // --- Signos vitales -------------------------------------------------
        // Paciente critico: deterioro progresivo a lo largo del turno.
        lucia.ifPresent(p -> {
            toma(p, enfDia, 118, 76, 72, 97, 36.6, 11);
            toma(p, enfDia, 148, 92, 104, 93, 37.8, 6);
            toma(p, enfDia, 195, 115, 132, 85, 39.4, 1.5);
        });
        hector.ifPresent(p -> {
            toma(p, enfNoche, 122, 78, 76, 96, 36.4, 9);
            toma(p, enfDia, 150, 94, 108, 92, 37.6, 4);
        });
        // Control vencido: su ultima medicion supera las seis horas.
        rosa.ifPresent(p -> toma(p, enfNoche, 116, 74, 70, 98, 36.5, 10));
        marco.ifPresent(p -> toma(p, enfNoche, 152, 96, 110, 91, 37.9, 8));
        elena.ifPresent(p -> toma(p, enfDia, 146, 90, 118, 93, 37.4, 5));
        julio.ifPresent(p -> {
            toma(p, enfDia, 120, 78, 74, 97, 36.7, 7);
            toma(p, enfDia, 124, 80, 78, 96, 36.8, 2);
        });
        carmen.ifPresent(p -> toma(p, enfDia, 188, 110, 128, 86, 38.9, 3));
        // Alberto (HC-48255) queda sin tomas a proposito: es el caso de un
        // paciente recien admitido al que todavia no se le registra un control.

        // --- Indicaciones medicas -------------------------------------------
        lucia.ifPresent(p -> indicacion(p, med, "Enoxaparina", "40 mg", "Subcutanea",
                "Cada 12 horas", 9, enfDia, 8.0));
        hector.ifPresent(p -> indicacion(p, med, "Furosemida", "40 mg", "Endovenosa",
                "Cada 8 horas", 6, null, null));
        elena.ifPresent(p -> indicacion(p, med, "Amiodarona", "150 mg", "Endovenosa",
                "Dosis unica", 4, null, null));

        // --- Eventos clinicos -----------------------------------------------
        lucia.ifPresent(p -> evento(p, enfDia, ClinicalEventType.MEDICATION_ADMINISTRATION,
                ClinicalEventSeverity.ROUTINE, "Enoxaparina administrada sin incidencias.",
                "Enoxaparina", "40 mg", 8));
        hector.ifPresent(p -> evento(p, enfDia, ClinicalEventType.CLINICAL_OBSERVATION,
                ClinicalEventSeverity.NOTABLE, "Disnea en reposo al movilizarse a la silla.",
                null, null, 3.5));
        carmen.ifPresent(p -> evento(p, enfDia, ClinicalEventType.CLINICAL_OBSERVATION,
                ClinicalEventSeverity.CRITICAL, "Desaturacion brusca durante la aspiracion de secreciones.",
                null, null, 2.5));

        // --- Traspasos SBAR --------------------------------------------------
        lucia.ifPresent(p -> traspaso(p, enfDia, enfNoche,
                "Paciente con crisis hipertensiva en curso y saturacion en descenso.",
                "Infarto agudo de miocardio con elevacion del ST, segundo dia de hospitalizacion.",
                "Requiere oxigeno suplementario y vigilancia continua del ritmo.",
                "Control de signos cada dos horas y avisar al medico de guardia ante nueva desaturacion.",
                1, null));
        rosa.ifPresent(p -> traspaso(p, enfNoche, enfDia,
                "Paciente estable, sin dolor ni requerimiento de oxigeno.",
                "Post operada de revascularizacion miocardica, cuarto dia.",
                "Evoluciona favorablemente, tolera la via oral.",
                "Continuar controles cada seis horas y favorecer la deambulacion asistida.",
                9, 8.5));

        log.info("Seeded the demo clinical history of the current shift");
    }

    // ---------------------------------------------------------------- apoyo

    private Instant hace(double horas) {
        return ahora.minusSeconds(Math.round(horas * 3600));
    }

    private Optional<PatientId> paciente(String historiaClinica) {
        return patients.handle(new GetPatientByMedicalRecordNumberQuery(historiaClinica))
                .map(p -> new PatientId(p.getId()));
    }

    private Optional<UserId> usuario(String username) {
        return users.handle(new GetUserByUsernameQuery(username)).map(u -> new UserId(u.getId()));
    }

    /** Guarda el agregado y publica los eventos que su creacion produce. */
    private void publicar(AbstractDomainAggregateRoot<?> agregado) {
        agregado.domainEvents().forEach(publisher::publishEvent);
        agregado.clearDomainEvents();
    }

    private void toma(PatientId paciente, UserId enfermera, int sistolica, int diastolica,
                      int frecuencia, int saturacion, double temperatura, double horasAtras) {
        var registro = VitalSignRecord.reconstitute(null, paciente, enfermera,
                new BloodPressure(sistolica, diastolica), frecuencia, saturacion, temperatura,
                null, hace(horasAtras));
        var guardado = vitalSigns.save(registro);
        guardado.registerCreationEvents();
        publicar(guardado);
    }

    private void indicacion(PatientId paciente, UserId medico, String medicamento, String dosis,
                            String via, String frecuencia, double horasAtras,
                            UserId cumplidaPor, Double horasAtrasCumplimiento) {
        var orden = MedicalOrder.reconstitute(null, paciente, medico,
                new Dosage(medicamento, dosis, via, frecuencia), OrderStatus.ACTIVE,
                hace(horasAtras), null, null,
                horasAtrasCumplimiento == null ? null : hace(horasAtrasCumplimiento), cumplidaPor);
        var guardada = medicalOrders.save(orden);
        guardada.registerCreationEvents();
        publicar(guardada);
    }

    private void evento(PatientId paciente, UserId autor, ClinicalEventType tipo,
                        ClinicalEventSeverity severidad, String descripcion,
                        String medicamento, String dosis, double horasAtras) {
        var registro = ClinicalEvent.reconstitute(null, paciente, autor, tipo, severidad,
                descripcion, medicamento, dosis, null, hace(horasAtras));
        var guardado = clinicalEvents.save(registro);
        guardado.registerCreationEvents();
        publicar(guardado);
    }

    private void traspaso(PatientId paciente, UserId saliente, UserId entrante,
                          String situacion, String antecedentes, String evaluacion,
                          String recomendacion, double horasAtras, Double horasAtrasAcuse) {
        var estado = horasAtrasAcuse == null ? HandoverStatus.ISSUED : HandoverStatus.ACKNOWLEDGED;
        var traspaso = Handover.reconstitute(null, paciente, saliente, entrante,
                new SbarContent(situacion, antecedentes, evaluacion, recomendacion),
                estado, hace(horasAtras), horasAtrasAcuse == null ? null : hace(horasAtrasAcuse));
        var guardado = handovers.save(traspaso);
        guardado.registerCreationEvents();
        publicar(guardado);
    }
}
