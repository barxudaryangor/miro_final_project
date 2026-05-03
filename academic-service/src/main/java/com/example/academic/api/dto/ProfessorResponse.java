package com.example.academic.api.dto;

import java.util.UUID;

public record ProfessorResponse(
        UUID id,
        UUID userId,
        String name,
        String surname,
        String department,
        String email
) {}
