package com.kaique.transacao_api.domain.ports;

import java.time.Duration;
import java.util.UUID;

public interface TokenIssuer {
    String issue(UUID userId, String email, Duration ttl);
}

