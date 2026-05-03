package com.example.academic.kafka;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record AcademicEvent(
        UUID eventId,
        AcademicEventType eventType,
        Instant occurredAt,
        UUID actorId,
        String actorRole,
        String actorEmail,
        String objectType,
        UUID objectId,
        UUID courseId,
        Map<String, Object> data
) {
    public static AcademicEvent create(
            AcademicEventType eventType,
            UUID actorId,
            String actorRole,
            String actorEmail,
            String objectType,
            UUID objectId,
            UUID courseId,
            Map<String, Object> data) {
        return new AcademicEvent(
                UUID.randomUUID(),
                eventType,
                Instant.now(),
                actorId,
                actorRole,
                actorEmail,
                objectType,
                objectId,
                courseId,
                data
        );
    }
}
