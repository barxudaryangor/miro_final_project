package com.example.academic.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AssignmentResponse(
        UUID id,
        String title,
        String description,
        LocalDateTime dueDate,
        UUID courseId,
        UUID professorId,
        LocalDateTime createdAt
) {}
