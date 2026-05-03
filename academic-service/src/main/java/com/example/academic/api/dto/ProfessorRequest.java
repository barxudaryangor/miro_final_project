package com.example.academic.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ProfessorRequest(
        @NotNull UUID userId,
        @NotBlank String name,
        @NotBlank String surname,
        @NotBlank String department,
        @NotBlank @Email String email
) {}
