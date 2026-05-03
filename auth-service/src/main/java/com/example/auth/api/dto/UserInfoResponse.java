package com.example.auth.api.dto;

import java.time.Instant;
import java.util.UUID;

public record UserInfoResponse(
        UUID id,
        String email,
        String role,
        Boolean isActive,
        Instant createdAt
) {}
