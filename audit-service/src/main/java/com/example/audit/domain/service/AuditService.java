package com.example.audit.domain.service;

import com.example.audit.api.dto.AuditLogResponse;
import com.example.audit.api.dto.AuditStatsResponse;
import com.example.audit.domain.model.AuditEventCommand;
import com.example.audit.kafka.AcademicEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditService {

    void saveAuditLog(AuditEventCommand command);

    Page<AuditLogResponse> getAuditLogs(String eventType, Pageable pageable);

    AuditStatsResponse getStats();
}
