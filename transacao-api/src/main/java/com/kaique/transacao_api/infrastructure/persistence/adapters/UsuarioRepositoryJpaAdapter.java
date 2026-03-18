package com.kaique.transacao_api.infrastructure.persistence.adapters;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.kaique.transacao_api.domain.model.Usuario;
import com.kaique.transacao_api.domain.ports.UsuarioRepository;
import com.kaique.transacao_api.infrastructure.persistence.jpa.entities.UsuarioJpaEntity;
import com.kaique.transacao_api.infrastructure.persistence.jpa.repositories.UsuarioJpaRepository;

@Component
public class UsuarioRepositoryJpaAdapter implements UsuarioRepository {

    private final UsuarioJpaRepository repository;
    private final Clock clock;

    public UsuarioRepositoryJpaAdapter(UsuarioJpaRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    @Override
    public Optional<Usuario> findById(UUID id) {
        return repository.findById(id).map(UsuarioRepositoryJpaAdapter::toDomain);
    }

    @Override
    public Optional<Usuario> findByEmail(String emailNormalizado) {
        return repository.findByEmail(emailNormalizado).map(UsuarioRepositoryJpaAdapter::toDomain);
    }

    @Override
    public boolean existsByEmail(String emailNormalizado) {
        return repository.existsByEmail(emailNormalizado);
    }

    @Override
    public Optional<Usuario> findByIdForUpdate(UUID id) {
        return repository.findByIdForUpdate(id).map(UsuarioRepositoryJpaAdapter::toDomain);
    }

    @Override
    public Usuario save(Usuario usuario) {
        OffsetDateTime now = OffsetDateTime.now(clock);
        UsuarioJpaEntity entity = repository.findById(usuario.getId()).orElseGet(UsuarioJpaEntity::new);
        entity.setId(usuario.getId());
        entity.setNome(usuario.getNome());
        entity.setEmail(usuario.getEmail());
        entity.setPasswordHash(usuario.getPasswordHash());
        entity.setSaldo(usuario.getSaldo());
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(now);
        }
        entity.setUpdatedAt(now);
        UsuarioJpaEntity saved = repository.save(entity);
        return toDomain(saved);
    }

    private static Usuario toDomain(UsuarioJpaEntity entity) {
        return new Usuario(entity.getId(), entity.getNome(), entity.getEmail(), entity.getPasswordHash(), entity.getSaldo());
    }
}

