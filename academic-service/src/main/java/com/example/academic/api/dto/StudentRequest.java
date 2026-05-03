package com.example.academic.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;
import java.util.UUID;

public record StudentRequest(
        @NotNull UUID userId,
        @NotBlank String name,
        @NotBlank String surname,
        String gender,
        @PastOrPresent LocalDate birthDate,
        @NotBlank @Email String email,
        @PastOrPresent LocalDate enrollDate,
        Boolean isActive
) {}
