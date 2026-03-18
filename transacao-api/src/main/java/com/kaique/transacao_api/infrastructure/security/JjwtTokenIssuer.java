package com.kaique.transacao_api.infrastructure.security;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.kaique.transacao_api.domain.ports.TokenIssuer;
import com.kaique.transacao_api.usecase.config.AppProperties;

import io.jsonwebtoken.Jwts;

@Component
public class JjwtTokenIssuer implements TokenIssuer {

    private final AppProperties properties;

    public JjwtTokenIssuer(AppProperties properties) {
        this.properties = properties;
    }

    @Override
    public String issue(UUID userId, String email, Duration ttl) {
        Instant now = Instant.now();
        Instant exp = now.plus(ttl);
        return Jwts.builder()
                .issuer(properties.getJwt().getIssuer())
                .subject(userId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .claim("email", email)
                .signWith(JwtSupport.hmacKeyFromSecret(properties.getJwt().getSecret()))
                .compact();
    }
}

