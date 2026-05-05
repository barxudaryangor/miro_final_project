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
        this.coursesCreated = Counter.builder("academic_courses_created_total")
                .description("Total number of courses successfully created")
                .register(meterRegistry);

        this.assignmentsCreated = Counter.builder("academic_assignments_created_total")
                .description("Total number of assignments successfully created")
                .register(meterRegistry);

        this.submissionsCreated = Counter.builder("academic_submissions_created_total")
                .description("Total number of assignment submissions successfully created")
                .register(meterRegistry);

        this.studentsRegistered = Counter.builder("academic_students_registered_total")
                .description("Total number of students successfully registered to a course")
                .register(meterRegistry);
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
