package com.example.academic.mapper;

import com.example.academic.api.dto.StudentRequest;
import com.example.academic.api.dto.StudentResponse;
import com.example.academic.persistence.entity.StudentEntity;

public class StudentMapper {

    public static StudentEntity toEntity(StudentRequest request) {
        return StudentEntity.builder()
                .userId(request.userId())
                .name(request.name())
                .surname(request.surname())
                .gender(request.gender())
                .birthDate(request.birthDate())
                .email(request.email())
                .enrollDate(request.enrollDate())
                .isActive(request.isActive())
                .build();
    }

    public static StudentResponse toResponse(StudentEntity entity) {
        return new StudentResponse(
                entity.getId(),
                entity.getUserId(),
                entity.getName(),
                entity.getSurname(),
                entity.getGender(),
                entity.getBirthDate(),
                entity.getEmail(),
                entity.getEnrollDate(),
                entity.getIsActive()
        );
    }

    public static void updateEntity(StudentEntity entity, StudentRequest request) {
        entity.setUserId(request.userId());
        entity.setName(request.name());
        entity.setSurname(request.surname());
        entity.setGender(request.gender());
        entity.setBirthDate(request.birthDate());
        entity.setEmail(request.email());
        entity.setEnrollDate(request.enrollDate());
        entity.setIsActive(request.isActive());
    }
}
