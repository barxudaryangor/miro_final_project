package com.example.auth.api.dto;

public record AuthResponse(
        String token,
        String email,
        String role
) {}
