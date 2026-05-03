package com.example.notification.kafka;

import com.example.notification.domain.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "${kafka.topics.academic-events:academic-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(AcademicEvent event) {
        log.info("Received academic event [eventId={}, eventType={}]",
                event.getEventId(), event.getEventType());

        notificationService.processEvent(event);
    }
}