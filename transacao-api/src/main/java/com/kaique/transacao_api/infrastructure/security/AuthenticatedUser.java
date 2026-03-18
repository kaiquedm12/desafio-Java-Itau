package com.kaique.transacao_api.infrastructure.security;

import java.util.UUID;

public record AuthenticatedUser(UUID userId, String email) {
}

