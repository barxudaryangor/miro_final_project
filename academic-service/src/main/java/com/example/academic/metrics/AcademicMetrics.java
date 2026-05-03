package com.example.academic.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class AcademicMetrics {

    private final Counter coursesCreated;
    private final Counter assignmentsCreated;
    private final Counter submissionsCreated;
    private final Counter studentsRegistered;

    public AcademicMetrics(MeterRegistry meterRegistry) {
        this.coursesCreated = meterRegistry.counter("academic_courses_created_total");
        this.assignmentsCreated = meterRegistry.counter("academic_assignments_created_total");
        this.submissionsCreated = meterRegistry.counter("academic_submissions_created_total");
        this.studentsRegistered = meterRegistry.counter("academic_students_registered_total");
    }

    public void incrementCoursesCreated() {
        coursesCreated.increment();
    }

    public void incrementAssignmentsCreated() {
        assignmentsCreated.increment();
    }

    public void incrementSubmissionsCreated() {
        submissionsCreated.increment();
    }

    public void incrementStudentsRegistered() {
        studentsRegistered.increment();
    }
}
