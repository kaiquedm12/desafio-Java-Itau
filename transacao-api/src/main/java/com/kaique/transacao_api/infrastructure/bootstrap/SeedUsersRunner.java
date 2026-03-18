package com.kaique.transacao_api.infrastructure.bootstrap;

import java.math.BigDecimal;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.kaique.transacao_api.domain.model.Usuario;
import com.kaique.transacao_api.domain.ports.PasswordHasher;
import com.kaique.transacao_api.domain.ports.UsuarioRepository;

@Component
@Profile("!test")
public class SeedUsersRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedUsersRunner.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordHasher passwordHasher;

    public SeedUsersRunner(UsuarioRepository usuarioRepository, PasswordHasher passwordHasher) {
        this.usuarioRepository = usuarioRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public void run(String... args) {
        String email = "admin@example.com";
        if (usuarioRepository.existsByEmail(email)) {
            return;
        }

        String password = "admin123";
        Usuario admin = new Usuario(UUID.randomUUID(), "Admin", email, passwordHasher.hash(password), new BigDecimal("0.00"));
        usuarioRepository.save(admin);
        log.warn("Seed user created: email={}", email);
    }
}
