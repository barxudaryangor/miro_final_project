package com.example.academic.api.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record AssignmentRequest(
        @NotBlank String title,
        String description,
        @NotNull @Future LocalDateTime dueDate
) {}
