package com.kaique.transacao_api.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.SecretKey;

import io.jsonwebtoken.security.Keys;

final class JwtSupport {

    private JwtSupport() {
    }

    static SecretKey hmacKeyFromSecret(String secret) {
        byte[] bytes = secret == null ? new byte[0] : secret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length >= 32) {
            return Keys.hmacShaKeyFor(bytes);
        }
        try {
            // Ensure we always have >= 256 bits, even in dev environments with short secrets.
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(bytes);
            return Keys.hmacShaKeyFor(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}

