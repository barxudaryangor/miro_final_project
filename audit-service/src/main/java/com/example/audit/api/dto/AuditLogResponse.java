package com.example.audit.api.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record AuditLogResponse(
        UUID id,
        UUID eventId,
        String eventType,
        UUID actorId,
        String actorRole,
        String objectType,
        UUID objectId,
        String action,
        String detailsJson,
        String actorEmail,
        UUID courseId,
        Instant createdAt
) {}
