package com.example.academic.api.dto;

import java.util.UUID;

public record StudentCourseRequest(
        UUID studentId,
        UUID courseId
) {}
