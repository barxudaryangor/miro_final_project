package com.example.academic.api.dto;

import java.util.UUID;

public record ActorContext(
        UUID actorId,
        String actorEmail,
        String actorRole
) {}
