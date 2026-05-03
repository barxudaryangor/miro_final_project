package com.example.audit.api.mapper;

import com.example.audit.api.dto.AuditLogResponse;
import com.example.audit.persistence.entity.AuditLogEntity;
import java.time.Instant;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-05-03T23:15:02+0400",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.18 (Amazon.com Inc.)"
)
@Component
public class AuditLogMapperImpl implements AuditLogMapper {

    @Override
    public AuditLogResponse toResponse(AuditLogEntity entity) {
        if ( entity == null ) {
            return null;
        }

        UUID id = null;
        UUID eventId = null;
        String eventType = null;
        UUID actorId = null;
        String actorRole = null;
        String objectType = null;
        UUID objectId = null;
        String action = null;
        String detailsJson = null;
        String actorEmail = null;
        UUID courseId = null;
        Instant createdAt = null;

        id = entity.getId();
        eventId = entity.getEventId();
        eventType = entity.getEventType();
        actorId = entity.getActorId();
        actorRole = entity.getActorRole();
        objectType = entity.getObjectType();
        objectId = entity.getObjectId();
        action = entity.getAction();
        detailsJson = entity.getDetailsJson();
        actorEmail = entity.getActorEmail();
        courseId = entity.getCourseId();
        createdAt = entity.getCreatedAt();

        AuditLogResponse auditLogResponse = new AuditLogResponse( id, eventId, eventType, actorId, actorRole, objectType, objectId, action, detailsJson, actorEmail, courseId, createdAt );

        return auditLogResponse;
    }
}
