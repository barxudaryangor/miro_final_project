package com.example.academic.api.dto;

import java.time.LocalDate;
import java.util.UUID;

public record StudentResponse(
        UUID id,
        UUID userId,
        String name,
        String surname,
        String gender,
        LocalDate birthDate,
        String email,
        LocalDate enrollDate,
        Boolean isActive
) {}
