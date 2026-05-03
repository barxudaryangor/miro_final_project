package com.example.academic.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AcademicEventPublisher {

    private final KafkaTemplate<String, AcademicEvent> kafkaTemplate;

    @Value("${kafka.topics.academic-events}")
    private String topic;

    public void publish(AcademicEvent event) {
        kafkaTemplate.send(topic, event.courseId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish event {} of type {}: {}",
                                event.eventId(), event.eventType(), ex.getMessage());
                    } else {
                        log.info("Published event {} of type {} to partition {} offset {}",
                                event.eventId(), event.eventType(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
}
