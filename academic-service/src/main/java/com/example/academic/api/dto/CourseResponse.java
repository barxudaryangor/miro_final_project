package com.example.academic.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CourseResponse(
        UUID id,
        String title,
        Integer credits,
        UUID professorId,
        LocalDateTime createdAt
) {}
