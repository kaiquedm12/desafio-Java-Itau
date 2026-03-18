package com.kaique.transacao_api.domain.ports;

public interface PasswordHasher {
    String hash(String rawPassword);

    boolean matches(String rawPassword, String passwordHash);
}

