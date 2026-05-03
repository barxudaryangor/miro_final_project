package com.example.notification.persistence.repository;

import com.example.notification.persistence.entity.NotificationLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationLogRepository extends JpaRepository<NotificationLogEntity, UUID> {

    boolean existsByEventId(UUID eventId);
}