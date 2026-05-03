package com.example.academic.mapper;

import com.example.academic.api.dto.AssignmentRequest;
import com.example.academic.api.dto.AssignmentResponse;
import com.example.academic.persistence.entity.AssignmentEntity;

public class AssignmentMapper {

    public static AssignmentEntity toEntity(AssignmentRequest request) {
        return AssignmentEntity.builder()
                .title(request.title())
                .description(request.description())
                .dueDate(request.dueDate())
                .build();
    }

    public static AssignmentResponse toResponse(AssignmentEntity entity) {
        return new AssignmentResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getDueDate(),
                entity.getCourseId(),
                entity.getProfessorId(),
                entity.getCreatedAt()
        );
    }

    public static void updateEntity(AssignmentEntity entity, AssignmentRequest request) {
        if (request.title() != null)       entity.setTitle(request.title());
        if (request.description() != null) entity.setDescription(request.description());
        if (request.dueDate() != null)     entity.setDueDate(request.dueDate());
    }
}
