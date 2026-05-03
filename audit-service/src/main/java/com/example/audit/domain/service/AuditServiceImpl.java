package com.example.audit.domain.service;

import com.example.audit.api.dto.AuditLogResponse;
import com.example.audit.api.dto.AuditStatsResponse;
import com.example.audit.api.mapper.AuditLogMapper;
import com.example.audit.domain.model.AuditEventCommand;
import com.example.audit.metrics.AuditMetrics;
import com.example.audit.persistence.entity.AuditLogEntity;
import com.example.audit.persistence.repository.AuditLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final AuditLogMapper auditLogMapper;
    private final AuditMetrics auditMetrics;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void saveAuditLog(AuditEventCommand command) {
        try {
            String detailsJson = null;
            if (command.data() != null) {
                detailsJson = objectMapper.writeValueAsString(command.data());
            }

            AuditLogEntity entity = AuditLogEntity.builder()
                    .eventId(command.eventId())
                    .eventType(command.eventType())
                    .actorId(command.actorId())
                    .actorRole(command.actorRole())
                    .objectType(command.objectType())
                    .objectId(command.objectId())
                    .occurredAt(
                            command.occurredAt() != null
                                    ? command.occurredAt()
                                    : Instant.now()
                    )
                    .action(extractAction(command.eventType()))
                    .actorEmail(command.actorEmail())
                    .courseId(command.courseId())
                    .detailsJson(detailsJson)
                    .build();

            auditLogRepository.save(entity);
            auditMetrics.incrementEventsSaved();
        } catch (DataIntegrityViolationException e) {
            log.info("Duplicate audit event skipped: eventId={}", command.eventId());

        } catch (JsonProcessingException e) {
            log.error("Failed to serialize event data for eventId={}", command.eventId(), e);
            throw new RuntimeException("Failed to serialize audit event data", e);

        } catch (Exception e) {
            log.error("Failed to save audit log for eventId={}", command.eventId(), e);
            throw new RuntimeException("Failed to save audit log", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogResponse> getAuditLogs(String eventType, Pageable pageable) {
        Page<AuditLogEntity> page;
        if (eventType != null && !eventType.isBlank()) {
            page = auditLogRepository.findByEventType(eventType, pageable);
        } else {
            page = auditLogRepository.findAll(pageable);
        }
        return page.map(auditLogMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public AuditStatsResponse getStats() {
        long totalEvents = auditLogRepository.count();
        long totalStudentCourseRegistrations = auditLogRepository.countByEventType("STUDENT_REGISTERED_TO_COURSE");
        long totalAssignmentsCreated = auditLogRepository.countByEventType("ASSIGNMENT_CREATED");
        long totalSubmissionsCreated = auditLogRepository.countByEventType("SUBMISSION_CREATED");
        long totalSubmissionsGraded = auditLogRepository.countByEventType("SUBMISSION_GRADED");

        return new AuditStatsResponse(
                totalEvents,
                totalStudentCourseRegistrations,
                totalAssignmentsCreated,
                totalSubmissionsCreated,
                totalSubmissionsGraded
        );
    }

    private String extractAction(String eventType) {
        if (eventType == null || eventType.isBlank()) {
            return "UNKNOWN";
        }

        if (eventType.endsWith("_CREATED")) {
            return "CREATE";
        }

        if (eventType.endsWith("_UPDATED")) {
            return "UPDATE";
        }

        if (eventType.endsWith("_DELETED")) {
            return "DELETE";
        }

        if (eventType.endsWith("_REGISTERED_TO_COURSE")) {
            return "REGISTER";
        }

        if (eventType.endsWith("_GRADED")) {
            return "GRADE";
        }

        return "OTHER";
    }
}
