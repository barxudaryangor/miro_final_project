package com.example.academic.mapper;

import com.example.academic.api.dto.ProfessorRequest;
import com.example.academic.api.dto.ProfessorResponse;
import com.example.academic.persistence.entity.ProfessorEntity;

public class ProfessorMapper {

    public static ProfessorEntity toEntity(ProfessorRequest request) {
        return ProfessorEntity.builder()
                .userId(request.userId())
                .name(request.name())
                .surname(request.surname())
                .department(request.department())
                .email(request.email())
                .build();
    }

    public static ProfessorResponse toResponse(ProfessorEntity entity) {
        return new ProfessorResponse(
                entity.getId(),
                entity.getUserId(),
                entity.getName(),
                entity.getSurname(),
                entity.getDepartment(),
                entity.getEmail()
        );
    }

    public static void updateEntity(ProfessorEntity entity, ProfessorRequest request) {
        entity.setUserId(request.userId());
        entity.setName(request.name());
        entity.setSurname(request.surname());
        entity.setDepartment(request.department());
        entity.setEmail(request.email());
    }
}
