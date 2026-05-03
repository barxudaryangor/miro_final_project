package com.example.academic.mapper;

import com.example.academic.api.dto.StudentCourseRequest;
import com.example.academic.api.dto.StudentCourseResponse;
import com.example.academic.persistence.entity.StudentCourseEntity;

import java.time.LocalDateTime;

public class StudentCourseMapper {

    public static StudentCourseEntity toEntity(StudentCourseRequest request) {
        return StudentCourseEntity.builder()
                .studentId(request.studentId())
                .courseId(request.courseId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static StudentCourseResponse toResponse(StudentCourseEntity entity) {
        return new StudentCourseResponse(
                entity.getId(),
                entity.getStudentId(),
                entity.getCourseId(),
                entity.getCreatedAt()
        );
    }
}
