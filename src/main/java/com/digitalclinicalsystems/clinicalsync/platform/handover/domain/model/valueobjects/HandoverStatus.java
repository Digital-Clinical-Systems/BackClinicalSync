package com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.valueobjects;

/**
 * DRAFT exists only while the outgoing nurse composes the handover in the client;
 * the API persists handovers already ISSUED.
 */
public enum HandoverStatus {
    DRAFT,
    ISSUED,
    ACKNOWLEDGED
}
