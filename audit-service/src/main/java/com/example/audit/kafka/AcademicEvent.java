package com.example.audit.kafka;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AcademicEvent {

    private UUID eventId;
    private String eventType;
    private Instant occurredAt;
    private UUID actorId;
    private String actorRole;
    private String actorEmail;
    private String objectType;
    private UUID objectId;
    private UUID courseId;
    private Map<String, Object> data;
}
