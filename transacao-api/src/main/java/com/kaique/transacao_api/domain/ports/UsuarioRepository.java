package com.kaique.transacao_api.domain.ports;

import java.util.Optional;
import java.util.UUID;

import com.kaique.transacao_api.domain.model.Usuario;

public interface UsuarioRepository {
    Optional<Usuario> findById(UUID id);

    Optional<Usuario> findByEmail(String emailNormalizado);

    boolean existsByEmail(String emailNormalizado);

    Optional<Usuario> findByIdForUpdate(UUID id);

    Usuario save(Usuario usuario);
}

