package com.example.audit.api.controller;

import com.example.audit.api.dto.AuditLogResponse;
import com.example.audit.api.dto.AuditStatsResponse;
import com.example.audit.domain.service.AuditService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@Tag(name = "Audit")
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/logs")
    public Page<AuditLogResponse> getLogs(
            @RequestParam(required = false) String eventType,
            Pageable pageable
    ) {
        return auditService.getAuditLogs(eventType, pageable);
    }

    @GetMapping("/stats")
    public AuditStatsResponse getStats() {
        return auditService.getStats();
    }
}
