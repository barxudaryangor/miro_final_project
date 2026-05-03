package com.example.audit.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "actor_id")
    private UUID actorId;

    @Column(name = "actor_role", length = 50)
    private String actorRole;

    @Column(name = "object_type", length = 100)
    private String objectType;

    @Column(name = "object_id")
    private UUID objectId;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    @Column(name = "action", length = 255)
    private String action;

    @Column(name = "details_json", columnDefinition = "TEXT")
    private String detailsJson;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "actor_email", length = 255)
    private String actorEmail;

    @Column(name = "course_id")
    private UUID courseId;

    @PrePersist
    private void prePersist() {
        createdAt = Instant.now();
    }
}
