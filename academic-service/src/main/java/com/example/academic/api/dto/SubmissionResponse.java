package com.example.academic.api.dto;

import com.example.academic.persistence.entity.SubmissionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SubmissionResponse(
        UUID id,
        UUID assignmentId,
        UUID studentId,
        String content,
        LocalDateTime submittedAt,
        BigDecimal grade,
        SubmissionStatus status
) {}
