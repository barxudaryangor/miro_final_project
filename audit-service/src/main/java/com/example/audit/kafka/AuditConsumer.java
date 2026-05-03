package com.example.audit.kafka;

import com.example.audit.domain.model.AuditEventCommand;
import com.example.audit.domain.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditConsumer {

    private final AuditService auditService;

    @KafkaListener(topics = "${kafka.topics.academic-events:academic-events}")
    public void consume(AcademicEvent event) {
        AuditEventCommand command = new AuditEventCommand(
                event.getEventId(),
                event.getEventType(),
                event.getActorId(),
                event.getActorRole(),
                event.getObjectType(),
                event.getObjectId(),
                event.getOccurredAt(),
                event.getActorEmail(),
                event.getCourseId(),
                event.getData()
        );

        log.debug("Received academic event: eventType={}, eventId={}",
                command.eventType(), command.eventId());

        auditService.saveAuditLog(command);
    }
}
