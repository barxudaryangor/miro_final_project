package com.example.academic.api.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record StudentCourseResponse(
        UUID id,
        UUID studentId,
        UUID courseId,
        LocalDateTime createdAt
) {}
