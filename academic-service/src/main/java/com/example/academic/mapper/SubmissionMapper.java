package com.example.academic.mapper;

import com.example.academic.api.dto.SubmissionRequest;
import com.example.academic.api.dto.SubmissionResponse;
import com.example.academic.persistence.entity.SubmissionEntity;
import com.example.academic.persistence.entity.SubmissionStatus;

public class SubmissionMapper {

    public static SubmissionEntity toEntity(SubmissionRequest request) {
        return SubmissionEntity.builder()
                .content(request.content())
                .status(SubmissionStatus.SUBMITTED)
                .build();
    }

    public static SubmissionResponse toResponse(SubmissionEntity entity) {
        return new SubmissionResponse(
                entity.getId(),
                entity.getAssignmentId(),
                entity.getStudentId(),
                entity.getContent(),
                entity.getSubmittedAt(),
                entity.getGrade(),
                entity.getStatus()
        );
    }

    public static void updateEntity(SubmissionEntity entity, SubmissionRequest request) {
        entity.setContent(request.content());
    }
}
