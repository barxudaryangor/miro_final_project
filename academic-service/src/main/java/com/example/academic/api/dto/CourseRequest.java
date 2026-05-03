package com.example.academic.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record CourseRequest(
        @NotBlank String title,
        @NotNull @Positive Integer credits,
        @NotNull UUID professorId
) {}
