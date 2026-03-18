package com.kaique.transacao_api.usecase.auth;

import java.time.Duration;

import org.springframework.stereotype.Service;

import com.kaique.transacao_api.domain.exceptions.UnauthorizedException;
import com.kaique.transacao_api.domain.model.Usuario;
import com.kaique.transacao_api.domain.ports.PasswordHasher;
import com.kaique.transacao_api.domain.ports.TokenIssuer;
import com.kaique.transacao_api.domain.ports.UsuarioRepository;
import com.kaique.transacao_api.usecase.config.AppProperties;
import com.kaique.transacao_api.usecase.validation.ValidationSupport;

@Service
public class AuthenticateUserUseCase {

    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher passwordHasher;
    private final TokenIssuer tokenIssuer;
    private final AppProperties properties;

    public AuthenticateUserUseCase(
            UsuarioRepository usuarioRepository,
            PasswordHasher passwordHasher,
            TokenIssuer tokenIssuer,
            AppProperties properties) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
        this.tokenIssuer = tokenIssuer;
        this.properties = properties;
    }

    public AuthResult login(String email, String senha) {
        String emailNormalizado = ValidationSupport.normalizarEmail(email);
        String senhaRaw = ValidationSupport.requireNonBlank(senha, "senha");

        Usuario usuario = usuarioRepository.findByEmail(emailNormalizado)
                .orElseThrow(() -> new UnauthorizedException("Credenciais invalidas."));

        if (!passwordHasher.matches(senhaRaw, usuario.getPasswordHash())) {
            throw new UnauthorizedException("Credenciais invalidas.");
        }

        Duration ttl = Duration.ofSeconds(properties.getJwt().getExpirationSeconds());
        String token = tokenIssuer.issue(usuario.getId(), usuario.getEmail(), ttl);
        return new AuthResult(token, properties.getJwt().getExpirationSeconds(), usuario.getId(), usuario.getEmail());
    }

    public record AuthResult(String accessToken, long expiresInSeconds, java.util.UUID userId, String email) {
    }
}

