package com.kaique.transacao_api.usecase.user;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.kaique.transacao_api.domain.exceptions.BusinessRuleViolationException;
import com.kaique.transacao_api.domain.model.Usuario;
import com.kaique.transacao_api.domain.ports.PasswordHasher;
import com.kaique.transacao_api.domain.ports.UsuarioRepository;
import com.kaique.transacao_api.usecase.config.AppProperties;
import com.kaique.transacao_api.usecase.validation.ValidationSupport;

@Service
public class CreateUserUseCase {

    private static final BigDecimal ZERO = new BigDecimal("0.00");
    private static final SecureRandom secureRandom = new SecureRandom();

    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher passwordHasher;
    private final AppProperties properties;

    public CreateUserUseCase(UsuarioRepository usuarioRepository, PasswordHasher passwordHasher, AppProperties properties) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
        this.properties = properties;
    }

    public CreateUserResult execute(String nome, String email, String senha) {
        String nomeValidado = ValidationSupport.validarNome(nome);
        String emailNormalizado = ValidationSupport.normalizarEmail(email);

        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            throw new BusinessRuleViolationException("Email ja cadastrado.");
        }

        String rawPassword;
        String temporaryPassword = null;
        if (senha == null || senha.isBlank()) {
            if (!properties.getUser().getRegistration().isAllowTemporaryPassword()) {
                throw new BusinessRuleViolationException("O campo senha e obrigatorio.");
            }
            rawPassword = gerarSenhaTemporaria();
            temporaryPassword = rawPassword;
        } else {
            rawPassword = senha.trim();
        }

        String hash = passwordHasher.hash(rawPassword);
        Usuario usuario = new Usuario(UUID.randomUUID(), nomeValidado, emailNormalizado, hash, ZERO);
        Usuario salvo = usuarioRepository.save(usuario);
        return new CreateUserResult(salvo, temporaryPassword);
    }

    private static String gerarSenhaTemporaria() {
        byte[] bytes = new byte[18];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public record CreateUserResult(Usuario usuario, String senhaTemporaria) {
    }
}
