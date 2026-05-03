package com.example.audit.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class AuditMetrics {

    private final Counter eventsSavedCounter;

    public AuditMetrics(MeterRegistry registry) {
        this.eventsSavedCounter = Counter.builder("audit_events_saved_total")
                .description("Total number of audit events saved")
                .register(registry);
    }

    public void incrementEventsSaved() {
        eventsSavedCounter.increment();
    }
}
