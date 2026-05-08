package com.example.auth.api.dto;

public record ErrorResponse(
        String code,
        String message,
        Object details)
{}
