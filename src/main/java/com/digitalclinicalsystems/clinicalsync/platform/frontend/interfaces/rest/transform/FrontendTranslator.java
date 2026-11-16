package com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.transform;

import com.digitalclinicalsystems.clinicalsync.platform.alerts.interfaces.rest.resources.AlertResource;
import com.digitalclinicalsystems.clinicalsync.platform.audit.interfaces.rest.resources.AuditLogResource;
import com.digitalclinicalsystems.clinicalsync.platform.clinicalevents.interfaces.rest.resources.ClinicalEventResource;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.application.internal.ExternalReferenceService;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.application.internal.ExternalReferenceService.Translation;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.application.internal.outboundservices.acl.ExternalIamService;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.application.internal.outboundservices.acl.ExternalIamService.Directory;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.application.internal.outboundservices.acl.ExternalIamService.Professional;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.domain.model.valueobjects.ReferenceType;
import com.digitalclinicalsystems.clinicalsync.platform.frontend.interfaces.rest.resources.*;
import com.digitalclinicalsystems.clinicalsync.platform.handover.interfaces.rest.resources.HandoverResource;
import com.digitalclinicalsystems.clinicalsync.platform.medicalorders.interfaces.rest.resources.MedicalOrderResource;
import com.digitalclinicalsystems.clinicalsync.platform.patients.interfaces.rest.resources.PatientResource;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.interfaces.rest.resources.VitalSignRecordResource;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

/**
 * Converts the resources of the bounded contexts into the contract the frontend
 * already consumes: identifiers as text (the ones the frontend generated, when it
 * created the record), professionals by username and audit actions with the
 * event names of the frontend domain.
 */
@Component
public class FrontendTranslator {

    /** Backend action type to the event name the frontend audit view labels. */
    private static final Map<String, String> ACTION_NAMES = Map.ofEntries(
            Map.entry("PATIENT_ADMITTED", "PacienteAdmitido"),
            Map.entry("PATIENT_DISCHARGED", "PacienteDadoDeAlta"),
            Map.entry("VITAL_SIGNS_RECORDED", "SignosVitalesRegistrados"),
            Map.entry("CLINICAL_RISK_LEVEL_EVALUATED", "NivelDeRiesgoClinicoEvaluado"),
            Map.entry("ALERT_RAISED", "AlertaCriticaGenerada"),
            Map.entry("ALERT_ACKNOWLEDGED", "AlertaAtendida"),
            Map.entry("ALERT_RESOLVED", "AlertaResuelta"),
            Map.entry("MEDICAL_ORDER_ISSUED", "NuevaIndicacionMedicaRegistrada"),
            Map.entry("MEDICAL_ORDER_SUPERSEDED", "IndicacionMedicaReemplazada"),
            Map.entry("MEDICAL_ORDER_FULFILLED", "IndicacionMedicaCumplida"),
            Map.entry("SBAR_HANDOVER_ISSUED", "EntregaSbarRegistrada"),
            Map.entry("HANDOVER_ACKNOWLEDGED", "AcuseDeReciboConfirmado"),
            Map.entry("CLINICAL_EVENT_RECORDED", "EventoClinicoRegistrado"),
            Map.entry("CRITICAL_CLINICAL_EVENT_RECORDED", "EventoClinicoCriticoRegistrado"));

    private static final String SYSTEM_ACTOR = "system";
    private static final String FRONTEND_SYSTEM_ACTOR = "sistema";

    private final ExternalReferenceService externalReferenceService;
    private final ExternalIamService externalIamService;

    public FrontendTranslator(ExternalReferenceService externalReferenceService,
                              ExternalIamService externalIamService) {
        this.externalReferenceService = externalReferenceService;
        this.externalIamService = externalIamService;
    }

    /** Snapshot of every translation, taken once per request. */
    public Context context() {
        var translations = new EnumMap<ReferenceType, Translation>(ReferenceType.class);
        for (var type : ReferenceType.values()) {
            translations.put(type, externalReferenceService.translation(type));
        }
        return new Context(translations, externalIamService.directory());
    }

    public record Context(Map<ReferenceType, Translation> translations, Directory directory) {

        public String id(ReferenceType type, Long internalId) {
            return translations.get(type).toExternal(internalId);
        }

        public String user(Long userId) {
            return directory.usernameOf(userId);
        }

        /** {@code vital-sign:12} becomes {@code vital-sign:<frontend id of record 12>}. */
        public String triggerSource(String triggerSource) {
            if (triggerSource == null || !triggerSource.contains(":")) {
                return triggerSource;
            }
            var prefix = triggerSource.substring(0, triggerSource.indexOf(':'));
            var reference = triggerSource.substring(triggerSource.indexOf(':') + 1);
            for (var type : ReferenceType.values()) {
                if (type.prefix().equals(prefix) && reference.chars().allMatch(Character::isDigit) && !reference.isEmpty()) {
                    return prefix + ":" + id(type, Long.valueOf(reference));
                }
            }
            return triggerSource;
        }

        public FrontendUserResource toResource(Professional professional) {
            return new FrontendUserResource(professional.username(), professional.fullName(),
                    professional.primaryRole().name(), professional.shift(), professional.specialty());
        }

        public FrontendPatientResource toResource(PatientResource patient) {
            return new FrontendPatientResource(id(ReferenceType.PATIENT, patient.id()), patient.medicalRecordNumber(),
                    patient.fullName(), patient.admissionDiagnosis() == null ? "" : patient.admissionDiagnosis(),
                    patient.age(), patient.unit(), patient.bed(), patient.admittedAt(), patient.active());
        }

        public FrontendVitalSignResource toResource(VitalSignRecordResource record) {
            return new FrontendVitalSignResource(id(ReferenceType.VITAL_SIGN, record.id()),
                    id(ReferenceType.PATIENT, record.patientId()), user(record.recordedBy()),
                    record.systolic(), record.diastolic(), record.heartRate(), record.oxygenSaturation(),
                    record.temperature(), record.meanArterialPressure(), record.riskLevel(), record.measuredAt(),
                    id(ReferenceType.VITAL_SIGN, record.correctsRecordId()));
        }

        public FrontendAlertResource toResource(AlertResource alert) {
            return new FrontendAlertResource(id(ReferenceType.ALERT, alert.id()),
                    id(ReferenceType.PATIENT, alert.patientId()), alert.severity(), triggerSource(alert.triggerSource()),
                    alert.reason(), alert.status(), alert.raisedAt(), user(alert.acknowledgedBy()));
        }

        public FrontendMedicalOrderResource toResource(MedicalOrderResource order) {
            return new FrontendMedicalOrderResource(id(ReferenceType.MEDICAL_ORDER, order.id()),
                    id(ReferenceType.PATIENT, order.patientId()), user(order.prescribedBy()),
                    order.medication(), order.dose(), order.route(), order.frequency(), order.status(),
                    order.prescribedAt(), id(ReferenceType.MEDICAL_ORDER, order.replacesOrderId()),
                    id(ReferenceType.MEDICAL_ORDER, order.supersededBy()), order.fulfilledAt(),
                    user(order.fulfilledBy()));
        }

        public FrontendHandoverResource toResource(HandoverResource handover) {
            return new FrontendHandoverResource(id(ReferenceType.HANDOVER, handover.id()),
                    id(ReferenceType.PATIENT, handover.patientId()), user(handover.outgoingNurseId()),
                    user(handover.incomingNurseId()), handover.situation(), handover.background(),
                    handover.assessment(), handover.recommendation(), handover.status(), handover.issuedAt(),
                    handover.acknowledgedAt());
        }

        public FrontendClinicalEventResource toResource(ClinicalEventResource event) {
            return new FrontendClinicalEventResource(id(ReferenceType.CLINICAL_EVENT, event.id()),
                    id(ReferenceType.PATIENT, event.patientId()), user(event.recordedBy()), event.type(),
                    event.severity(), event.description(), event.medication(), event.dose(),
                    id(ReferenceType.MEDICAL_ORDER, event.relatedOrderId()), event.occurredAt());
        }

        /** The frontend audit view filters by patient, so the affected resource is the patient id. */
        public FrontendAuditLogResource toResource(AuditLogResource auditLog) {
            var actor = SYSTEM_ACTOR.equals(auditLog.actorId()) || auditLog.actorId() == null
                    ? FRONTEND_SYSTEM_ACTOR
                    : auditLog.actorId().chars().allMatch(Character::isDigit)
                    ? user(Long.valueOf(auditLog.actorId()))
                    : auditLog.actorId();
            var affected = auditLog.patientId() != null
                    ? id(ReferenceType.PATIENT, auditLog.patientId())
                    : auditLog.affectedResource();
            return new FrontendAuditLogResource(String.valueOf(auditLog.id()),
                    ACTION_NAMES.getOrDefault(auditLog.actionType(), auditLog.actionType()), auditLog.occurredAt(),
                    affected, actor, auditLog.metadata());
        }
    }
}
