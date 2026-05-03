package com.example.notification.domain.service;

import com.example.notification.kafka.AcademicEvent;

public interface NotificationService {

    void processEvent(AcademicEvent event);
}
