package com.example.academic.api.dto;

import jakarta.validation.constraints.NotBlank;

public record SubmissionRequest(
        @NotBlank String content
) {}
