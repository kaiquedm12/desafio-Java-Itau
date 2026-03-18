package com.kaique.transacao_api.usecase.user;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.kaique.transacao_api.domain.exceptions.NotFoundException;
import com.kaique.transacao_api.domain.model.Usuario;
import com.kaique.transacao_api.domain.ports.UsuarioRepository;
import com.kaique.transacao_api.usecase.validation.ValidationSupport;

@Service
public class GetUserUseCase {

    private final UsuarioRepository usuarioRepository;

    public GetUserUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario execute(UUID id) {
        ValidationSupport.requireId(id, "id");
        return usuarioRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuario nao encontrado."));
    }
}

