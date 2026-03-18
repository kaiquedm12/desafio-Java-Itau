package com.kaique.transacao_api.controller.dtos;

import java.util.UUID;

public record AuthLoginResponseDTO(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        UUID userId,
        String email
) {
}

