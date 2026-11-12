package com.digitalclinicalsystems.clinicalsync.platform.shared.domain.model.events;

import java.time.Instant;

/**
 * Published Language shared by every bounded context. Each domain event states
 * who acted, on which patient and on which record, so BC-06 (Audit) can turn any
 * event into an audit entry without importing the publishing context.
 */
public interface DomainEvent {

    /** Actor used when the system, not a user, produced the event. */
    String SYSTEM_ACTOR = "system";

    /** Patient the event refers to, or {@code null} when it is not patient-related. */
    Long patientId();

    /** Id of the user that caused the event, or {@link #SYSTEM_ACTOR}. */
    String actorId();

    /** Affected record expressed as {@code type:id}, for example {@code vital-sign:12}. */
    String affectedResource();

    Instant occurredAt();

    static String actorOf(Long userId) {
        return userId == null ? SYSTEM_ACTOR : String.valueOf(userId);
    }
}
