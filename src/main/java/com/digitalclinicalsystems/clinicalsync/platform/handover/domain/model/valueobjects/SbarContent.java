package com.digitalclinicalsystems.clinicalsync.platform.handover.domain.model.valueobjects;

import java.util.ArrayList;

/**
 * Value object of BC-05. None of the four SBAR sections can be empty.
 */
public record SbarContent(String situation, String background, String assessment, String recommendation) {

    public SbarContent {
        var empty = new ArrayList<String>();
        if (isBlank(situation)) empty.add("Situation");
        if (isBlank(background)) empty.add("Background");
        if (isBlank(assessment)) empty.add("Assessment");
        if (isBlank(recommendation)) empty.add("Recommendation");
        if (!empty.isEmpty()) {
            throw new IllegalArgumentException("An SBAR handover cannot be issued with empty sections: " + String.join(", ", empty));
        }
        situation = situation.trim();
        background = background.trim();
        assessment = assessment.trim();
        recommendation = recommendation.trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
