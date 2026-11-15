package com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.aggregates;

import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.AbstractDomainAggregateRoot;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.PatientId;
import com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.valueobjects.UserId;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.commands.RecordVitalSignsCommand;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.events.ClinicalRiskLevelEvaluatedEvent;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.events.VitalSignsRecordedEvent;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.valueobjects.BloodPressure;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.model.valueobjects.RiskLevel;
import com.digitalclinicalsystems.clinicalsync.platform.vitalsigns.domain.services.RiskEvaluator;

import java.time.Instant;
import java.util.ArrayList;

/**
 * Aggregate root of BC-03 (core domain).
 * Invariants: references a patient and the responsible user; values fall in the
 * physiological range; the risk level is derived and never accepted from outside;
 * the record is immutable once created (a correction is a new record).
 */
public class VitalSignRecord extends AbstractDomainAggregateRoot<VitalSignRecord> {

    public static final int HEART_RATE_MIN = 20;
    public static final int HEART_RATE_MAX = 250;
    public static final int OXYGEN_SATURATION_MIN = 50;
    public static final int OXYGEN_SATURATION_MAX = 100;
    public static final double TEMPERATURE_MIN = 30;
    public static final double TEMPERATURE_MAX = 43;

    private Long id;
    private final PatientId patientId;
    private final UserId recordedBy;
    private final BloodPressure bloodPressure;
    private final int heartRate;
    private final int oxygenSaturation;
    private final double temperature;
    private final Long correctsRecordId;
    private final RiskLevel riskLevel;
    private Instant measuredAt;

    public VitalSignRecord(RecordVitalSignsCommand command) {
        this(command.patientId(), command.recordedBy(), new BloodPressure(command.systolic(), command.diastolic()),
                command.heartRate(), command.oxygenSaturation(), command.temperature(), command.correctsRecordId());
        this.measuredAt = Instant.now();
    }

    private VitalSignRecord(PatientId patientId, UserId recordedBy, BloodPressure bloodPressure,
                            Integer heartRate, Integer oxygenSaturation, Double temperature, Long correctsRecordId) {
        if (patientId == null) {
            throw new IllegalArgumentException("A vital sign record must reference a patient");
        }
        if (recordedBy == null) {
            throw new IllegalArgumentException("A vital sign record must reference the responsible user");
        }
        if (heartRate == null || heartRate < HEART_RATE_MIN || heartRate > HEART_RATE_MAX) {
            throw new IllegalArgumentException("Heart rate out of physiological range (%d-%d bpm)"
                    .formatted(HEART_RATE_MIN, HEART_RATE_MAX));
        }
        if (oxygenSaturation == null || oxygenSaturation < OXYGEN_SATURATION_MIN || oxygenSaturation > OXYGEN_SATURATION_MAX) {
            throw new IllegalArgumentException("Oxygen saturation out of range (%d-%d %%)"
                    .formatted(OXYGEN_SATURATION_MIN, OXYGEN_SATURATION_MAX));
        }
        if (temperature == null || temperature.isNaN() || temperature < TEMPERATURE_MIN || temperature > TEMPERATURE_MAX) {
            throw new IllegalArgumentException("Temperature out of physiological range (30-43 C)");
        }
        this.patientId = patientId;
        this.recordedBy = recordedBy;
        this.bloodPressure = bloodPressure;
        this.heartRate = heartRate;
        this.oxygenSaturation = oxygenSaturation;
        this.temperature = temperature;
        this.correctsRecordId = correctsRecordId;
        this.riskLevel = RiskEvaluator.evaluate(bloodPressure, heartRate, oxygenSaturation, temperature);
    }

    /**
     * Rebuilds a stored measurement without publishing events. The invariants
     * are validated again and the risk level is derived again, so persistence can
     * never contradict the clinical rule.
     */
    public static VitalSignRecord reconstitute(
            Long id,
            PatientId patientId,
            UserId recordedBy,
            BloodPressure bloodPressure,
            Integer heartRate,
            Integer oxygenSaturation,
            Double temperature,
            Long correctsRecordId,
            Instant measuredAt) {
        var record = new VitalSignRecord(patientId, recordedBy, bloodPressure, heartRate, oxygenSaturation,
                temperature, correctsRecordId);
        record.id = id;
        record.measuredAt = measuredAt;
        return record;
    }

    /** Records the registration and risk evaluation events once the record has an identity. */
    public void registerCreationEvents() {
        registerDomainEvents(new VitalSignsRecordedEvent(
                id, patientId.patientId(), recordedBy.userId(), riskLevel.name(), correctsRecordId, measuredAt));
        registerDomainEvents(new ClinicalRiskLevelEvaluatedEvent(
                id, patientId.patientId(), recordedBy.userId(), riskLevel.name(),
                "vital-sign:" + id, outOfRangeSummary(), measuredAt));
    }

    public boolean isCorrection() {
        return correctsRecordId != null;
    }

    /**
     * Describes the values out of threshold. Built by the aggregate because it is
     * the only one that knows the thresholds it was evaluated with.
     */
    public String outOfRangeSummary() {
        var causes = new ArrayList<String>();
        var systolic = bloodPressure.systolic();
        var meanArterialPressure = bloodPressure.meanArterialPressure();
        if (oxygenSaturation < 90) causes.add("oxygen saturation at %d %%".formatted(oxygenSaturation));
        else if (oxygenSaturation < 94) causes.add("decreasing oxygen saturation, %d %%".formatted(oxygenSaturation));
        if (heartRate > 130) causes.add("heart rate at %d bpm".formatted(heartRate));
        else if (heartRate > 110) causes.add("tachycardia of %d bpm".formatted(heartRate));
        if (heartRate < 40) causes.add("bradycardia of %d bpm".formatted(heartRate));
        else if (heartRate < 50) causes.add("low heart rate, %d bpm".formatted(heartRate));
        if (systolic > 180) causes.add("systolic pressure at %d mmHg".formatted(systolic));
        else if (systolic > 160) causes.add("high systolic pressure, %d mmHg".formatted(systolic));
        if (systolic < 90) causes.add("hypotension with systolic pressure at %d mmHg".formatted(systolic));
        else if (systolic < 100) causes.add("low systolic pressure, %d mmHg".formatted(systolic));
        if (meanArterialPressure < 65) causes.add("mean arterial pressure at %d mmHg".formatted(meanArterialPressure));
        if (temperature >= 39) causes.add("temperature at %s C".formatted(temperature));
        else if (temperature >= 38) causes.add("low-grade fever of %s C".formatted(temperature));
        if (temperature < 35) causes.add("hypothermia of %s C".formatted(temperature));
        return causes.isEmpty() ? "Values within range" : "Out of threshold: " + String.join("; ", causes);
    }

    public Long getId() {
        return id;
    }

    public PatientId getPatientId() {
        return patientId;
    }

    public UserId getRecordedBy() {
        return recordedBy;
    }

    public BloodPressure getBloodPressure() {
        return bloodPressure;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public int getOxygenSaturation() {
        return oxygenSaturation;
    }

    public double getTemperature() {
        return temperature;
    }

    public Long getCorrectsRecordId() {
        return correctsRecordId;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public Instant getMeasuredAt() {
        return measuredAt;
    }
}
