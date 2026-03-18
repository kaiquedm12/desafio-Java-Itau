package com.kaique.transacao_api.infrastructure.security;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.kaique.transacao_api.usecase.config.AppProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SecurityException;

@Component
public class JwtTokenParser {

    private final AppProperties properties;

    public JwtTokenParser(AppProperties properties) {
        this.properties = properties;
    }

    public AuthenticatedUser parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(JwtSupport.hmacKeyFromSecret(properties.getJwt().getSecret()))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String subject = claims.getSubject();
            String email = claims.get("email", String.class);
            return new AuthenticatedUser(UUID.fromString(subject), email);
        } catch (SecurityException | IllegalArgumentException e) {
            throw new JwtValidationException("Invalid token", e);
        }
    }
}

