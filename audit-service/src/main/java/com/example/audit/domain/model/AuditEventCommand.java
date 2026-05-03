package com.example.audit.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record AuditEventCommand(
        UUID eventId,
        String eventType,
        UUID actorId,
        String actorRole,
        String objectType,
        UUID objectId,
        Instant occurredAt,
        String actorEmail,
        UUID courseId,
        Map<String, Object> data
) {
}