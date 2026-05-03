package com.example.notification.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class NotificationMetrics {

    private final Counter sentCounter;
    private final Counter failedCounter;

    public NotificationMetrics(MeterRegistry meterRegistry) {
        this.sentCounter = meterRegistry.counter("notifications_sent_total");
        this.failedCounter = meterRegistry.counter("notifications_failed_total");
    }

    public void incrementSent() {
        sentCounter.increment();
    }

    public void incrementFailed() {
        failedCounter.increment();
    }
}
