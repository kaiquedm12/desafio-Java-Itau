package com.kaique.transacao_api.infrastructure.security;

public class JwtValidationException extends RuntimeException {

    public JwtValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}

