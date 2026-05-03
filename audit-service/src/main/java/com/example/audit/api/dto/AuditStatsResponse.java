package com.example.audit.api.dto;

public record AuditStatsResponse(
        long totalEvents,
        long totalStudentCourseRegistrations,
        long totalAssignmentsCreated,
        long totalSubmissionsCreated,
        long totalSubmissionsGraded
) {}
