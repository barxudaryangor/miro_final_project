package com.example.academic.mapper;

import com.example.academic.api.dto.CourseRequest;
import com.example.academic.api.dto.CourseResponse;
import com.example.academic.persistence.entity.CourseEntity;

public class CourseMapper {

    public static CourseEntity toEntity(CourseRequest request) {
        return CourseEntity.builder()
                .title(request.title())
                .credits(request.credits())
                .professorId(request.professorId())
                .build();
    }

    public static CourseResponse toResponse(CourseEntity entity) {
        return new CourseResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getCredits(),
                entity.getProfessorId(),
                entity.getCreatedAt()
        );
    }

    public static void updateEntity(CourseEntity entity, CourseRequest request) {
        entity.setTitle(request.title());
        entity.setCredits(request.credits());
        entity.setProfessorId(request.professorId());
    }
}
