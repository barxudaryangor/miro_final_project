package com.example.notification.domain.service;

import com.example.notification.domain.model.NotificationStatus;
import com.example.notification.kafka.AcademicEvent;
import com.example.notification.metrics.NotificationMetrics;
import com.example.notification.persistence.entity.NotificationLogEntity;
import com.example.notification.persistence.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationLogRepository notificationLogRepository;
    private final NotificationMetrics notificationMetrics;

    @Override
    @Transactional
    public void processEvent(AcademicEvent event) {
        if (notificationLogRepository.existsByEventId(event.getEventId())) {
            log.info("Skipping duplicate academic event [eventId={}]", event.getEventId());
            return;
        }

        try {
            String message = buildMessage(event);
            String recipientEmail = resolveRecipientEmail(event);

            NotificationLogEntity entry = NotificationLogEntity.builder()
                    .eventId(event.getEventId())
                    .recipientEmail(recipientEmail)
                    .type(event.getEventType())
                    .message(message)
                    .status(NotificationStatus.PENDING)
                    .build();

            entry = notificationLogRepository.save(entry);

            entry.setStatus(NotificationStatus.SENT);
            notificationLogRepository.save(entry);

            incrementSentMetric(event);

        }
        catch (DataIntegrityViolationException ex) {
            log.info("Skipping duplicate academic event [eventId={}]", event.getEventId());
        }
        catch (Exception ex) {
            log.error("Failed to process academic event [eventId={}, type={}]: {}",
                    event.getEventId(), event.getEventType(), ex.getMessage(), ex);

            NotificationLogEntity failedEntry = NotificationLogEntity.builder()
                    .eventId(event.getEventId())
                    .recipientEmail(safeRecipientEmail(event))
                    .type(event.getEventType())
                    .message(null)
                    .status(NotificationStatus.FAILED)
                    .build();

            notificationLogRepository.save(failedEntry);
            incrementFailedMetric(event);
        }
    }

    private String resolveRecipientEmail(AcademicEvent event) {
        Map<String, Object> data = event.getData() != null ? event.getData() : Map.of();

        return switch (event.getEventType()) {
            case "STUDENT_REGISTERED_TO_COURSE" -> getRequiredString(data, "studentEmail");

            case "ASSIGNMENT_CREATED" -> event.getActorEmail();

            case "SUBMISSION_CREATED" -> getRequiredString(data, "teacherEmail");

            case "SUBMISSION_GRADED" -> getRequiredString(data, "studentEmail");

            default -> event.getActorEmail();
        };
    }

    private String safeRecipientEmail(AcademicEvent event) {
        try {
            return resolveRecipientEmail(event);
        } catch (Exception ex) {
            return event.getActorEmail();
        }
    }

    private String buildMessage(AcademicEvent event) {
        Map<String, Object> data = event.getData() != null ? event.getData() : Map.of();

        return switch (event.getEventType()) {
            case "STUDENT_REGISTERED_TO_COURSE" -> String.format(
                    "Student %s has been registered to course %s",
                    getRequiredString(data, "studentId"),
                    getRequiredString(data, "courseId"));

            case "ASSIGNMENT_CREATED" -> String.format(
                    "New assignment '%s' created in course %s",
                    getRequiredString(data, "title"),
                    getRequiredString(data, "courseId"));

            case "SUBMISSION_CREATED" -> String.format(
                    "Student %s submitted assignment %s",
                    getRequiredString(data, "studentId"),
                    getRequiredString(data, "assignmentId"));

            case "SUBMISSION_GRADED" -> String.format(
                    "Submission %s graded with %s for student %s",
                    getRequiredString(data, "submissionId"),
                    getRequiredString(data, "grade"),
                    getRequiredString(data, "studentId"));

            default -> String.format("Event of type '%s' received", event.getEventType());
        };
    }

    private String getRequiredString(Map<String, Object> data, String key) {
        Object value = data.get(key);

        if (value == null) {
            throw new IllegalArgumentException("Missing required field in event data: " + key);
        }

        return value.toString();
    }

    private void incrementSentMetric(AcademicEvent event) {
        try {
            notificationMetrics.incrementSent();
        } catch (Exception ex) {
            log.warn("Failed to increment sent metric [eventId={}]", event.getEventId(), ex);
        }
    }

    private void incrementFailedMetric(AcademicEvent event) {
        try {
            notificationMetrics.incrementFailed();
        } catch (Exception ex) {
            log.warn("Failed to increment failed metric [eventId={}]", event.getEventId(), ex);
        }
    }
}